package com.songlian.logistics.controller;


import cn.hutool.json.JSON;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.OrderForm;
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

    @PostMapping("/comptude")
    public Result compute(@RequestBody HashMap map){
        return orderFormService.genOrder(map);
    }
}

