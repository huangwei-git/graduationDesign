package com.songlian.logistics.service.impl;

import cn.hutool.core.lang.hash.Hash;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.calculate.ACO_TSP.ACO_TSP;
import com.songlian.logistics.calculate.EchartsDataTransfer.GraphDataTransfer;
import com.songlian.logistics.calculate.GA_TSP.GA_TSP;
import com.songlian.logistics.calculate.IP_TSP.IP_TSP;
import com.songlian.logistics.calculate.TabuSearch_TSP.TS_TSP;
import com.songlian.logistics.calculate.Transport.Calc;
import com.songlian.logistics.calculate.Transport.Path;
import com.songlian.logistics.calculate.Transport.TransportionSolution;
import com.songlian.logistics.calculate.TspData;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.OrderFormDao;
import com.songlian.logistics.pojo.*;
import com.songlian.logistics.service.InventoryService;
import com.songlian.logistics.service.LocationService;
import com.songlian.logistics.service.OrderFormService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class OrderFormServiceImpl extends ServiceImpl<OrderFormDao, OrderForm> implements OrderFormService {
    @Resource
    private LocationService locationService;

    @Resource
    private InventoryService inventoryService;

    @Autowired
    private OrderFormDao orderFormDao;

    // 缓存生成的订单
    private HashMap<String,List<HashMap>> orderBuffer = new HashMap<>();

    @Override
    public Result pageList(QueryPageParam query) {
        try {
            // 获取分页参数
            long size = (long) query.getPageSize();
            long num = (long) query.getPageNum();
            // 排序参数
            String sortField = (String) query.getParams().get("sortField");
            String sortDirection = (String) query.getParams().get("sortDirection");

            // 实例对象参数
            String orderId = "%" + (String) query.getParams().get("orderId") + "%";
            String materialName = "%" + (String) query.getParams().get("materialName") + "%";

            List<HashMap> data =  orderFormDao.orderList(orderId, materialName,sortField,sortDirection,(num - 1) * size,size);
            long listTotal = this.list().size();
            long pageTotal = orderFormDao.orderList
                    (orderId,
                            materialName,
                            sortField,
                            sortDirection,0l,listTotal).size();

            return Result.success(data,pageTotal);
        } catch (Exception e) {
            System.out.println("MaterialService.listByPage:" + e);
            return Result.fail("查询订单失败，请稍后重试");
        }
    }

    /**
     * 根据需求量、库存量、交通网的运费，计算出使得运费最小的运输方案
     * map:
     *     需要的物品id
     *     各需求地的需求量
     * @param map
     * @return
     */
    @Override
    public Result genOrder(HashMap map) {
        // 获得所有 运输中心 与 需求地
        List<Location> supplierLocationList = new LinkedList<>();
        List<Location> demanderLocationList = new LinkedList<>();
        locationService.list().forEach(location -> {
            if(location.getType() == 0) supplierLocationList.add(location);
            else demanderLocationList.add(location);
        });
        // 计算距离
        int cost[][] = Calc.getCostArray(supplierLocationList, demanderLocationList);
        // 计算需求地的距离
        int distDemander[][] = Calc.getCostArray(demanderLocationList,demanderLocationList);

        // 获取 供应量 数组
        LambdaQueryWrapper<Inventory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Inventory::getMaterialId, map.get("mid"));
        lqw.select(Inventory::getAmount);
        List<Inventory> list = inventoryService.list(lqw);
        int[] supplier = new int[list.size()];
        for(int i = 0;i < list.size();i++) supplier[i] = list.get(i).getAmount();

        // 获取 需求量 数组
        int demander[] = new int[demanderLocationList.size()];
        ArrayList<Integer> needList = (ArrayList<Integer>) map.get("need");
        for(int i = 0;i < needList.size();i++){
            demander[i] = needList.get(i);
        }

        TransportionSolution transportionSolution = new TransportionSolution(supplier,demander,cost,true);
        // 控制台输出优化调整过程
        transportionSolution.setOpenProcessPrint(true);
        //System.out.println(transportionSolution.getRecordPlan().size());
        transportionSolution.Run();

        /* 获取最终方案前的方案 */
        List<List<HashMap>> adjustProcessList = new LinkedList<>();
        adjustProcessList = this.getAdjustProcess(transportionSolution.getRecordPlan(),supplier.length,demander.length,supplierLocationList,demanderLocationList,demander,cost);
        /* 获得每个方案的运费 */
        List<Integer> recordTotal = transportionSolution.getRecordTotal();


        int[][] plan = transportionSolution.getPlanTab();
        System.out.println("total = " + transportionSolution.getTotal());
        /* 输出最终运输方案 */
        //for(int i = 0;i < supplier.length;i++){
        //    for(int j = 0;j < demander.length;j++){
        //        System.out.print(plan[i][j] + "  ");
        //    }
        //    System.out.println();
        //}

        // 获取各个运输中心的配送信息


        List<HashMap> detailList = new LinkedList<>();
        // 用于存储前端中，确认订单时的映射字段
        List<HashMap> requireList = new LinkedList<>();
        Set<HashMap> fieldMap = new TreeSet<>(new Comparator<HashMap>() {
            @Override
            public int compare(HashMap o1, HashMap o2) {
                try {
                    String prop1 = (String) o1.get("prop");
                    String prop2 = (String) o2.get("prop");
                    if(prop1.equals("storeNum")) return 1;
                    else if(prop2.equals("storeNum")) return -1;
                    if(prop1.equals("sendLocName")) return -1;
                    else if(prop2.equals("sendLocName")) return 1;
                    else{
                        int val1 = Integer.parseInt(prop1.substring(5,prop1.length()));
                        int val2 = Integer.parseInt(prop2.substring(5, prop2.length()));
                        return val1 - val2;
                    }
                }catch (Exception e){
                    return -1;
                }
            }
        });
        // 记录运费
        int[] tolls = new int[demander.length];
        for (int i = 0; i < supplier.length; i++) {
            List<HashMap> supplierDetail = new LinkedList<>();
            int cnt = 0;
            // 用于存储前端中，确认订单时的映射字段
            HashMap requireMap = new HashMap();
            requireMap.put("sendLocName",supplierLocationList.get(i).getName());
            requireMap.put("storeNum",supplier[i]);
            for (int j = 0; j < demander.length; j++) {
                if(demander[j] == 0) break;
                if(plan[i][j]!=0){
                    tolls[j] += plan[i][j] * cost[i][j];
                    cnt++;
                    HashMap detail = new HashMap<>();
                    // 从哪里运输
                    detail.put("supplierLocId", supplierLocationList.get(i).getLocId());
                    // 运输到哪
                    detail.put("demanderLocId", demanderLocationList.get(j).getLocId());
                    // 运输量
                    detail.put("amount", plan[i][j]);
                    // 路费
                    detail.put("toll", cost[i][j] * 10);

                    /* 建表时不要这两个字段！ */
                    detail.put("demanderName",demanderLocationList.get(j).getName());
                    detail.put("supplierName",supplierLocationList.get(i).getName());

                    // 当前运输地的索引，用于计算TSP路径时，获取点与点直接的距离
                    detail.put("seq",j);

                    supplierDetail.add(detail);
                }
                /*前端确认订单时的展示信息*/
                requireMap.put("count" + demanderLocationList.get(j).getLocId(),plan[i][j]);
            }
            requireList.add(requireMap);
            // 当运输目的地大于1个时，用TSP算法计算运输路径
            if(cnt > 1){
                int arr[] = Path.getPath(supplierDetail,distDemander);
                for(int j = 0;j < supplierDetail.size();j++){
                    // 覆盖原先的index键，用来表示访问的顺序
                    supplierDetail.get(j).put("seq", arr[j] + 1);
                    detailList.add(supplierDetail.get(j));
                }
            }else if(cnt == 1){
                supplierDetail.get(0).put("seq",1);
                detailList.add(supplierDetail.get(0));
            }
        }
        detailList.forEach(item -> {
            System.out.println(item);
        });
        System.out.println("========================");
        requireList.forEach(item -> {
            System.out.println(item);
        });

        /* 前端生成表格的表头数据解析映射 */
        Iterator iterator = requireList.get(0).keySet().iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            HashMap tmpMap = new HashMap();
            if(key.indexOf("count") != -1){
                int id = Integer.parseInt(key.substring(5, key.length()));
                String label = locationService.getById(id).getName();
                tmpMap.put("label",label);
                //tmpMap.put("prop",key);
            }else if(key.indexOf("send") != -1){
                tmpMap.put("label","运输中心");
                //tmpMap.put("prop",key);
            }else{
                tmpMap.put("label","库存量");
            }
            tmpMap.put("prop",key);
            fieldMap.add(tmpMap);
        }

        // 添加一行：各需求的需求量记录
        HashMap tmpMap = new HashMap();
        tmpMap.put("sendLocName","需求量");
        for(int i = 0;i < demander.length;i++){
            String key = "count" + demanderLocationList.get(i).getLocId();
            int value = demander[i];
            tmpMap.put(key,value);
        }
        requireList.add(tmpMap);

        /* 将运费加入tableData中 */
        // 调整过程的运费处理
        for (int i = 0; i < adjustProcessList.size(); i++) {
            List<HashMap> mapList = adjustProcessList.get(i);
            HashMap tollMap = mapList.get(mapList.size() - 1);
            tollMap.put("storeNum",recordTotal.get(i));
            //adjustProcessList.get(i).add(tollMap);
        }
        // 最终分配方案的运费处理
        HashMap tollMap = new HashMap();
        for(int j = 0;j < demander.length;j++){
            tollMap.put("count" + demanderLocationList.get(j).getLocId(),tolls[j]);
        }
        tollMap.put("storeNum",recordTotal.get(recordTotal.size() - 1));
        tollMap.put("sendLocName","运费");
        requireList.add(tollMap);
        adjustProcessList.add(requireList);

        System.out.println(fieldMap);



        HashMap resMap =new HashMap();
        resMap.put("key",saveOrder(detailList));
        resMap.put("fieldMap",fieldMap);
        resMap.put("tableDatas",adjustProcessList);
        return Result.success(resMap,supplier.length);
    }

    @Override
    public Result genOrderOfTsp(HashMap map) {
        // 缓存用于创建订单
        List<HashMap> resultBuff = new ArrayList<>();
        int amount = 0;

        // 获得配送地ID
        Integer locId = (Integer) map.get("locId");
        // 获得材料ID
        Integer materialId = (Integer) map.get("mid");
        // 获取 {需求量id 与 需求量} 数组
        ArrayList<Map> needList = (ArrayList<Map>) map.get("need");
        // 获得需要配送的需求地
        List<Location> locations = new ArrayList<>();
        // 存储坐标的数组
        double locationArray[][] = new double[needList.size() + 1][2];
        // 获取运输中心的location对象
        Location supplierLocation = locationService.getById(locId);
        // 存储地名
        List<String> locNameList = new ArrayList<>();
        locationArray[0][0] = supplierLocation.getXpos();
        locationArray[0][1] = supplierLocation.getYpos();
        locNameList.add(supplierLocation.getName());
        for (int i = 0; i < needList.size(); i++) {
            amount = amount + (Integer) needList.get(i).get("amount");
            Integer tmpLocId = (Integer) needList.get(i).get("locId");
            Location tmpLocation = locationService.getById(tmpLocId);
            locations.add(tmpLocation);
            locationArray[i + 1][0] = tmpLocation.getXpos();
            locationArray[i + 1][1] = tmpLocation.getYpos();
            locNameList.add(tmpLocation.getName());
        }

        List<double[]> locList = new ArrayList<>();
        Collections.addAll(locList,locationArray);
        // 初始化距离
        int cityNum = locList.size();
        double dist[][] = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            for (int j = i; j < cityNum; j++) {
                if (i != j) {
                    dist[i][j] = getDistance(locList.get(i), locList.get(j));
                    dist[j][i] = dist[i][j];
                }
            }
        }

        // 保存结果
        Map result = new HashMap();
        // 保存路径
        String paths[] = new String[4];

        //System.out.println("==========禁忌搜索算法==========");
        try {
            TspData TS_Data = new TS_TSP(locList, dist).solve();
            result.put("TSmin",TS_Data.getMinLength());
            paths[0] = TS_Data.getPath();
            result.put("TSpath",GraphDataTransfer.graphLinkFormat(TS_Data.getPath()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //System.out.println("==========蚁群算法==========");
        TspData ACO_Data = new ACO_TSP(locList, dist).solve();
        result.put("ACOmin",ACO_Data.getMinLength());
        paths[1] = ACO_Data.getPath();
        result.put("ACOpath",GraphDataTransfer.graphLinkFormat(ACO_Data.getPath()));

        //System.out.println("==========遗传算法==========");
        try {
            TspData GA_Data = new GA_TSP(locList, dist).solve();
            result.put("GAmin", GA_Data.getMinLength());
            paths[2] = GA_Data.getPath();
            result.put("GApath",GraphDataTransfer.graphLinkFormat(GA_Data.getPath()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        //System.out.println("==========数学规划==========");
        TspData IP_Data = new IP_TSP(locList, dist).solve();
        result.put("IPmin", IP_Data.getMinLength());
        paths[3] = IP_Data.getPath();
        result.put("IPpath",GraphDataTransfer.graphLinkFormat(IP_Data.getPath()));


        result.put("points", GraphDataTransfer.graphDataFormat(locList,locNameList));


        // 缓存结果
        HashMap bufferMap = new HashMap();
        // 运输地
        bufferMap.put("locId", locId);
        // 物品
        bufferMap.put("mid",materialId);
        // 总数
        bufferMap.put("amount", amount);
        // 距离矩阵
        bufferMap.put("dist", dist);
        // 需求地ID 和 需求量
        bufferMap.put("needList", needList);
        // 路径
        bufferMap.put("paths",paths);
        resultBuff.add(bufferMap);
        // 将结果缓存并获得key
        result.put("key",saveOrder(resultBuff));
        return Result.success(result);
    }

    // 将订单缓存，并返回一个key
    private String saveOrder(List<HashMap> orderDetail) {
        String key = LocalDateTime.now().toString();
        orderBuffer.put(key,orderDetail);
        return key;
    }

    // 从缓存中取出订单缓存
    public List<HashMap> getOrderBuffer(String key) {
        List<HashMap> res = orderBuffer.get(key);
        orderBuffer.remove(key);
        return res;
    }

    public List<List<HashMap>> getAdjustProcess(List<int[][]> plans,
                                                int rowLen,
                                                int colLen,
                                                List<Location> suppliersLoc,
                                                List<Location> demandersLoc,
                                                int[] demanders,
                                                int[][] cost
    ) {

        List<List<HashMap>> res = new LinkedList<>();
        for (int t = 0; t < plans.size() - 1; t++) {
            int toll[] = new int[colLen];
            int[][] plan = plans.get(t);
            List<HashMap> list = new LinkedList<>();
            for (int i = 0; i < rowLen; i++) {
                HashMap map = new HashMap();
                map.put("sendLocName", suppliersLoc.get(i).getName());
                boolean isEmpty = false;
                for (int j = 0; j < colLen; j++) {
                    if(demanders[j] == 0){
                        isEmpty=true;
                        break;
                    }
                    if(plan[i][j] != 0){
                        toll[j] += plan[i][j] * cost[i][j];
                        map.put("count" + demandersLoc.get(j).getLocId(), plan[i][j]);
                    }
                }
                list.add(map);
            }
            HashMap tempMap = new HashMap();
            for (int j = 0; j < colLen; j++) {
                if(demanders[j] != 0) tempMap.put("count"+demandersLoc.get(j).getLocId(),toll[j]);
            }
            tempMap.put("sendLocName", "运费");
            list.add(tempMap);

            //tempMap.put("")
            res.add(list);
        }
        return res;
    }

    // 计算两点之间的距离（使用伪欧氏距离，可以减少计算量）
    private double getDistance(double[] place1, double[] place2) {
        // 伪欧氏距离在根号内除以了一个10
        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)) / 10.0);
//        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)));
    }

    private Integer s2i(String field, Integer errValue) {
        Integer res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Integer.parseInt(field);
            } catch (Exception e) {
                return errValue;
            }
        }
        return res;
    }

    private Long s2l(String field, Long errValue) {
        Long res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Long.parseLong(field);
            } catch (Exception e) {
                return errValue;
            }
        }
        return res;
    }

    private Double s2d(String field, Double errValue) {
        Double res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Double.parseDouble(field);
            } catch (Exception e) {
                return errValue;
            }
        }
        return res;
    }

}