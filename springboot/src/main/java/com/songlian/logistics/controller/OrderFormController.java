package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.OrderDetail;
import com.songlian.logistics.pojo.OrderForm;
import com.songlian.logistics.service.OrderFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
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
@RequestMapping("/order_form")
public class OrderFormController {
    @Autowired
    private OrderFormService orderFormService;

    // 查询全部
    @GetMapping
    public List<OrderForm> list() {
        List<OrderForm> list = orderFormService.list();
        list.forEach(orderForm -> {
            System.out.println(orderForm);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public OrderForm selectById(@PathVariable Integer id){
        return orderFormService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody OrderForm orderForm){
        System.out.println(orderForm);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderForm.getOrderId());
        orderDetail.set
        return orderFormService.save(orderForm);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody OrderForm orderForm){
        return orderFormService.updateById(orderForm);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return orderFormService.removeById(id);
    }
}

