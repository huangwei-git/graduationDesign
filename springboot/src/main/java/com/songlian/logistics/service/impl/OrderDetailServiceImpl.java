package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.OrderDetailDao;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.pojo.OrderDetail;
import com.songlian.logistics.service.InventoryService;
import com.songlian.logistics.service.OrderDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

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
            return Result.success(data,pageTotal);
        } catch (Exception e) {
            System.out.println("MaterialService.listByPage:" + e);
            return Result.fail("查询订单详情失败，请稍后重试");
        }
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
