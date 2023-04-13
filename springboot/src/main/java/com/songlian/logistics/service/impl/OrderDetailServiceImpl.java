package com.songlian.logistics.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.OrderDetailDao;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.pojo.OrderDetail;
import com.songlian.logistics.pojo.OrderForm;
import com.songlian.logistics.service.InventoryService;
import com.songlian.logistics.service.OrderDetailService;
import com.songlian.logistics.service.OrderFormService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {

    @Resource
    private InventoryService inventoryService;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private OrderFormService orderFormService;

    private List currentPageData = null;

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
            String supplierName = "%" + (String) query.getParams().get("supplierName") + "%";
            String demanderName = "%" + (String) query.getParams().get("demanderName") + "%";

            List<HashMap> data =  orderDetailDao.orderDetailList(orderId, materialName,supplierName,demanderName,sortField,sortDirection,(num - 1) * size,size);

            long listTotal = this.list().size();
            long pageTotal = orderDetailDao.orderDetailList(orderId, materialName,supplierName,demanderName,sortField,sortDirection,0l,listTotal).size();

            currentPageData = data;

            return Result.success(data,pageTotal);
        } catch (Exception e) {
            System.out.println("MaterialService.listByPage:" + e);
            return Result.fail("查询订单详情失败，请稍后重试");
        }
    }

    @Override
    public void exportData(HttpServletResponse response) throws IOException {
        // 1. 从数据库中查出所有数据
        List list = currentPageData;

        // 2. 创建工具类，写出到浏览器
        // hutool.cn/docs/#/poi/Excel生成-ExcelWriter
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 3. 定义表头映射字段
        excelWriter.addHeaderAlias("orderId","订单编号");
        excelWriter.addHeaderAlias("materialName","物品名称");
        excelWriter.addHeaderAlias("amount","数量");
        excelWriter.addHeaderAlias("seq","运输顺序");
        excelWriter.addHeaderAlias("supplierName","发货地");
        excelWriter.addHeaderAlias("demanderName","收货地");
        excelWriter.addHeaderAlias("cost","物品成本");
        excelWriter.addHeaderAlias("price","物品价格");
        excelWriter.addHeaderAlias("toll","运费");
        excelWriter.addHeaderAlias("profit","利润");
        excelWriter.addHeaderAlias("total","总价");
        // 4. 写出list内容到Excel文件使用默认样式，强制输出标题
        excelWriter.write(list,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("订单详情","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");


        ServletOutputStream out = response.getOutputStream();
        excelWriter.flush(out, true);
        out.close();
        excelWriter.close();
    }


    @Override
    public Result createOrderDetail(Long id,int materialId,List<HashMap> list) {
        list.forEach(item -> {
            OrderDetail orderDetail = new OrderDetail();
            Integer demanderLocId = (Integer) item.get("demanderLocId");
            Integer supplierLocId = (Integer) item.get("supplierLocId");
            Integer amount = (Integer) item.get("amount");
            Integer toll = (Integer) item.get("toll");
            Integer seq = (Integer) item.get("seq");

            LambdaQueryWrapper<Inventory> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Inventory::getLocSendId,supplierLocId);
            lqw.eq(Inventory::getMaterialId,materialId);
            Inventory inventory = inventoryService.getOne(lqw);
            inventory.setAmount(inventory.getAmount() - amount);
            inventoryService.updateById(inventory);

            orderDetail.setOrderId(id);
            orderDetail.setDemanderLocId(demanderLocId);
            orderDetail.setSupplierLocId(supplierLocId);
            orderDetail.setAmount(amount);
            orderDetail.setToll(toll);
            orderDetail.setSeq(seq);

            this.save(orderDetail);
        });
        HashMap map = new HashMap();
        map.put("id",id);
        return Result.success(map);
    }

    @Override
    @Transactional
    public Result createOrderDetailByAppoint(String tspMethod, OrderForm orderForm, List<HashMap> list) {
        Map data = list.get(0);

        orderFormService.save(orderForm);
        // 订单ID
        Long id = orderForm.getOrderId();
        // 获取路径
        String paths[] = (String[]) data.get("paths");
        String path = null;
        if(tspMethod.equals("TS")) path = paths[0];
        else if(tspMethod.equals("ACO")) path = paths[1];
        else if(tspMethod.equals("GA")) path = paths[2];
        else if(tspMethod.equals("IP")) path = paths[3];
        else throw new RequestExpcetion(Constants.CODE_500, "TSP算法选择的参数错误！");
        // 获取运输地在路径中的位置
        int startIndex = 0;
        path = path.substring(1,path.length() - 2);
        path = path.replaceAll(" ","");
        String pathLoc[] = path.split(",");
        int pathLen = pathLoc.length;
        for(;startIndex < pathLen && !pathLoc[startIndex].equals("0");startIndex++);
        List<Integer> locationOrdered = new ArrayList<>();
        // 获取各个需求地的访问顺序
        for (int i = 1; i < pathLen; i++) {
            int locIndex = (startIndex + i) % pathLen;
            locationOrdered.add(Integer.valueOf(pathLoc[locIndex]));
        }
        // 存储各个位置对应的运费
        List<Integer> tollList = new ArrayList<>();
        // 取出距离表
        double dist[][] = (double[][]) data.get("dist");
        for (int i = 1; i < dist.length; i++) {
            tollList.add((int) dist[0][i]);
        }
        // 获取 需求地id、需求量 表
        ArrayList<Map> needList = (ArrayList<Map>) data.get("needList");
        // 获取运输地ID
        Integer locSendId = (Integer) data.get("locId");
        // 保存订单详情实体对象
        List<OrderDetail> orderDetailList = new ArrayList<>();
        // 遍历需求地
        for (int i = 1; i <= locationOrdered.size(); i++) {
            // 当前的需求地在其他表中的索引
            int idx = locationOrdered.get(i - 1);
            OrderDetail orderDetail = new OrderDetail();
            // 订单id
            orderDetail.setOrderId(id);
            // 供应地ID
            orderDetail.setSupplierLocId(locSendId);
            // 需求地ID
            orderDetail.setDemanderLocId((Integer) needList.get(idx - 1).get("locId"));
            // 需求量
            orderDetail.setAmount((Integer) needList.get(idx - 1).get("amount"));
            // 运费
            orderDetail.setToll(tollList.get(idx - 1));
            // 顺序
            orderDetail.setSeq(i);
            orderDetailList.add(orderDetail);
            System.out.println("orderDetail = " + orderDetail);
        }
        this.saveBatch(orderDetailList);
        // 从仓库中减去对应数量的物品
        LambdaQueryWrapper<Inventory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Inventory::getLocSendId,locSendId);
        lqw.eq(Inventory::getMaterialId,orderForm.getMaterialId());
        Inventory inventory = inventoryService.getOne(lqw);
        inventory.setAmount(inventory.getAmount() - orderForm.getAmount());
        inventoryService.updateById(inventory);

        Map resData = new HashMap(){{put("id",String.valueOf(orderForm.getOrderId()));}};
        return Result.success(resData);
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
