package com.songlian.logistics.controller;


import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.pojo.OrderForm;
import com.songlian.logistics.service.OrderDetailService;
import com.songlian.logistics.service.OrderFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

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
    public Result genAutoDistributionOrder(@RequestBody HashMap map){
        return orderFormService.genOrder(map);
    }

    @PostMapping("/tsp")
    public Result genAppointDistributionOrder(@RequestBody HashMap map){
        return orderFormService.genOrderOfTsp(map);
    }

    @PostMapping("/genOrderFromBuffer")
    public Result genAutoDistributionOrderFromBuffer(@RequestBody HashMap map) {
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
            //LambdaQueryWrapper<OrderForm> lqw = new LambdaQueryWrapper<>();
            //lqw.eq(OrderForm::getAmount,amount);
            //lqw.eq(OrderForm::getMaterialId,mid);
            //Long id = orderFormService.list(lqw).get(0).getOrderId();
            Long id = orderForm.getOrderId();
            return orderDetailService.createOrderDetail(id,mid,orderFormService.getOrderBuffer((String) map.get("key")));
        }
    }

    @PostMapping("/appointDistributionOrderBuffer")
    public Result genAppointDistributionOrderFromBuffer(@RequestBody Map map) {
        // 创建一个订单页面实例
        OrderForm orderForm = new OrderForm();
        Integer mid = null;
        Integer amount = null;
        String TspMethod = null;
        // 获取物品ID、物品数量
        try {
            mid = (Integer) map.get("mid");
            amount = (Integer) map.get("amount");
            TspMethod = (String) map.get("tspMethod");
        }catch (Exception e){
            return Result.fail("客户端传回的参数错误！");
        }
        // 设置实例属性
        orderForm.setMaterialId(mid);
        orderForm.setAmount(amount);
        try {
            return orderDetailService.createOrderDetailByAppoint(TspMethod,orderForm, orderFormService.getOrderBuffer((String) map.get("key")));
        }catch (RequestExpcetion re){
            System.out.println("客户端参数异常！ ： " + re);
            return Result.fail(re.getMessage());
        }catch (Exception e){
            System.out.println("添加订单失败！ ： " + e);
            return Result.fail("服务器异常，请稍后重试");
        }
    }

    // 导出
    @GetMapping("/export")
    public void exportData(HttpServletResponse response) throws Exception {
        orderFormService.exportData(response);
    }
}

