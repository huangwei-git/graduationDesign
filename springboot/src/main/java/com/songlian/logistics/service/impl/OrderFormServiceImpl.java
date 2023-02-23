package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.calculate.Transport.Calc;
import com.songlian.logistics.calculate.Transport.Path;
import com.songlian.logistics.calculate.Transport.TransportionSolution;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.OrderFormDao;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.pojo.OrderForm;
import com.songlian.logistics.service.InventoryService;
import com.songlian.logistics.service.LocationService;
import com.songlian.logistics.service.OrderFormService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

    @Override
    public Result pageList(QueryPageParam query) {
        try {
            System.out.println("query = " + query);
            // 获取分页参数
            long size = (long) query.getPageSize();
            long num = (long) query.getPageNum();
            // 排序参数
            String sortField = (String) query.getParams().get("sortField");
            String sortDirection = (String) query.getParams().get("sortDirection");

            // 实例对象参数
            Long orderId = s2l((String) query.getParams().get("orderId"), -1l);
            if (orderId != null && orderId == -1l) return Result.fail("orderId参数错误");

            Integer managerId = s2i((String) query.getParams().get("managerId"), -1);
            if (managerId != null && managerId == -1) return Result.fail("managerId参数错误");

            Integer price = s2i((String) query.getParams().get("price"), -1);
            if (price != null && price == -1) return Result.fail("price参数错误");

            Integer cost = s2i((String) query.getParams().get("cost"), -1);
            if (cost != null && cost == -1) return Result.fail("cost参数错误");

            Integer volume = s2i((String) query.getParams().get("volume"), -1);
            if (volume != null && volume == -1) return Result.fail("volume参数错误");


            // 初始化分页信息
            Page<Material> page = new Page<>();
            page.setSize(size);
            page.setCurrent(num);
            // 查询条件
            LambdaQueryWrapper<Material> lqw = new LambdaQueryWrapper();
            if (!sortField.equals("null") && StringUtils.isNotBlank(sortField)) {
                if (sortDirection.equals("1")) {
                    if (sortField.equals("sortField")) lqw.orderByDesc(Material::getMaterialId);
                    else if (sortField.equals("name")) lqw.orderByDesc(Material::getName);
                    else if (sortField.equals("cost")) lqw.orderByDesc(Material::getCost);
                    else if (sortField.equals("price")) lqw.orderByDesc(Material::getPrice);
                    else if (sortField.equals("volume")) lqw.orderByDesc(Material::getVolume);
                } else {
                    if (sortField.equals("sortField")) lqw.orderByAsc(Material::getMaterialId);
                    else if (sortField.equals("name")) lqw.orderByAsc(Material::getName);
                    else if (sortField.equals("cost")) lqw.orderByAsc(Material::getCost);
                    else if (sortField.equals("price")) lqw.orderByAsc(Material::getPrice);
                    else if (sortField.equals("volume")) lqw.orderByAsc(Material::getVolume);
                }
            }
            //lqw.eq(mid != null,Material::getMaterialId,mid);
            //lqw.like(!name.equals("null") && StringUtils.isNoneBlank(name),Material::getName,name);
            //lqw.eq(cost != null ,Material::getCost,cost);
            //lqw.eq(price != null, Material::getPrice, price);
            //lqw.eq(volume != null,Material::getVolume,volume);

            /*
            IPage iPage = this.page(page, lqw);
            return Result.success(iPage.getRecords(),iPage.getTotal());
            */
            return Result.fail("TEST-LocationService");
        } catch (Exception e) {
            System.out.println("MaterialService.listByPage:" + e);
            return Result.fail(e.toString());
        }
    }

    @Override
    public Result genOrder(HashMap map) {
        // 获得所有 运输中心 与 需求地
        List<Location> transports = new LinkedList<>();
        List<Location> demands = new LinkedList<>();
        locationService.list().forEach(location -> {
            if(location.getType() == 0) transports.add(location);
            else demands.add(location);
        });
        // 计算距离
        int cost[][] = Calc.getCostArray(transports, demands);
        // 计算需求地的距离
        int distDemander[][] = Calc.getCostArray(demands,demands);

        //System.out.println("################################");
        //for (int i = 0; i < distDemander.length; i++) {
        //    for (int j = 0; j < distDemander[0].length; j++) {
        //        System.out.print(distDemander[i][j]+"  ");
        //    }
        //    System.out.println();
        //}
        //System.out.println("################################");


        // 获取 供应量 数组
        LambdaQueryWrapper<Inventory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Inventory::getMaterialId, map.get("mid"));
        lqw.select(Inventory::getAmount);
        List<Inventory> list = inventoryService.list(lqw);
        int[] supplier = new int[list.size()];
        for(int i = 0;i < list.size();i++) supplier[i] = list.get(i).getAmount();
        for (int i = 0; i < supplier.length; i++) System.out.print(supplier[i] + " ");

        // 获取 需求量 数组
        int demander[] = new int[demands.size()];
        ArrayList<Integer> needList = (ArrayList<Integer>) map.get("need");
        for(int i = 0;i < needList.size();i++){
            demander[i] = needList.get(i);
        }

        TransportionSolution transportionSolution = new TransportionSolution(supplier,demander,cost,true);
        transportionSolution.Run();
        int[][] plan = transportionSolution.getPlanTab();
        System.out.println("total = " + transportionSolution.getTotal());
        for(int i = 0;i < supplier.length;i++){
            for(int j = 0;j < demander.length;j++){
                System.out.print(plan[i][j] + "  ");
            }
            System.out.println();
        }

        // 获取各个运输中心的配送信息
        List<HashMap> detailList = new LinkedList<>();
        for (int i = 0; i < supplier.length; i++) {
            List<HashMap> supplierDetail = new LinkedList<>();
            int cnt = 0;
            for (int j = 0; j < demander.length; j++) {
                if(plan[i][j]!=0){
                    cnt++;
                    HashMap detail = new HashMap<>();
                    // 从哪里运输
                    detail.put("supplierLocId", transports.get(i).getLocId());
                    // 运输到哪
                    detail.put("demanderLocId", demands.get(j).getLocId());
                    // 运输量
                    detail.put("count", plan[i][j]);
                    // 路费
                    detail.put("toll", cost[i][j] * 10);

                    /* 建表时不要这两个字段！ */
                    detail.put("demanderName",demands.get(j).getName());
                    detail.put("supplierName",transports.get(i).getName());
                    /**/

                    // 当前运输地的索引，用于计算TSP路径时，获取点与点直接的距离
                    detail.put("index",j);

                    supplierDetail.add(detail);
                }
            }
            // 当运输目的地大于1个时，用TSP算法计算运输路径
            if(cnt > 1){
                int arr[] = Path.getPath(supplierDetail,distDemander);
                for(int j = 0;j < supplierDetail.size();j++){
                    // 覆盖原先的index键，用来表示访问的顺序
                    supplierDetail.get(j).put("index", arr[j] + 1);
                    detailList.add(supplierDetail.get(j));
                }
            }else{
                supplierDetail.get(0).put("index",1);
                detailList.add(supplierDetail.get(0));
            }
        }
        detailList.forEach(item -> {
            System.out.println(item);
        });

        return Result.success();
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