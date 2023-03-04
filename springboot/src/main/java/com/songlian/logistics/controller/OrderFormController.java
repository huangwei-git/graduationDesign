package com.songlian.logistics.controller;


import cn.hutool.db.sql.Order;
import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.OrderForm;
import com.songlian.logistics.service.OrderDetailService;
import com.songlian.logistics.service.OrderFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@RestController
@RequestMapping("/order")
public class OrderFormController {
    @Autowired
    private OrderFormService orderFormService;

    @Autowired
    private OrderDetailService orderDetailService;


    @PostMapping("/listPage")
    public Result pageList(@RequestBody QueryPageParam query){
        return orderFormService.pageList(query);
    }

    @PostMapping("/compute")
    public Result compute(@RequestBody HashMap map){
        return orderFormService.genOrder(map);
    }

    @PostMapping("/tsp")
    public Result TSP(@RequestBody HashMap map){
        return orderFormService.genOrderOfTsp(map);
    }

    @PostMapping("/genOrderFromBuffer")
    public Result genOrderFromBuffer(@RequestBody HashMap map) {
        if((Integer)map.get("state") == 0){
            return Result.success();
        }else{
            Integer mid = (Integer) map.get("mid");
            Integer amount = (Integer) map.get("amount");
            /**
             * 还不知道怎么获取生成的ASIGN_ID，先偷个懒
             */
            OrderForm orderForm = new OrderForm();
            orderForm.setAmount(amount);
            orderForm.setMaterialId(mid);
            orderFormService.save(orderForm);
            LambdaQueryWrapper<OrderForm> lqw = new LambdaQueryWrapper<>();
            lqw.eq(OrderForm::getAmount,amount);
            lqw.eq(OrderForm::getMaterialId,mid);
            Long id = orderFormService.list(lqw).get(0).getOrderId();
            return orderDetailService.createOrderDetail(id,mid,orderFormService.getOrderBuffer((String) map.get("key")));
        }
    }

}

