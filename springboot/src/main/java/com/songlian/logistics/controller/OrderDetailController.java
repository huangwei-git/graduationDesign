package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.OrderDetail;
import com.songlian.logistics.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/order_detail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    // 查询全部
    @GetMapping
    public List<OrderDetail> list() {
        List<OrderDetail> list = orderDetailService.list();
        list.forEach(orderDetail -> {
            System.out.println(orderDetail);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public OrderDetail selectById(@PathVariable Integer id){
        return orderDetailService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody OrderDetail orderDetail){
        return orderDetailService.save(orderDetail);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody OrderDetail orderDetail){
        return orderDetailService.updateById(orderDetail);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return orderDetailService.removeById(id);
    }
}

