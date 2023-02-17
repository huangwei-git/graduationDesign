package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.Delivery;
import com.songlian.logistics.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // 查询全部
    @GetMapping
    public List<Delivery> list() {
        List<Delivery> list = deliveryService.list();
        list.forEach(delivery -> {
            System.out.println(delivery);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Delivery selectById(@PathVariable Integer id){
        return deliveryService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody Delivery delivery){
        return deliveryService.save(delivery);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Delivery delivery){
        return deliveryService.updateById(delivery);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return deliveryService.removeById(id);
    }
}

