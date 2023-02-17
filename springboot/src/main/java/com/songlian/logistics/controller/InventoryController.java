package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.service.InventoryService;
import com.songlian.logistics.service.InventoryService;
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
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    // 查询全部
    @GetMapping
    public List<Inventory> list() {
        List<Inventory> list = inventoryService.list();
        list.forEach(inventory -> {
            System.out.println(inventory);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Inventory selectById(@PathVariable Integer id){
            return inventoryService.getById(id);
    }

    // 新增
    @PostMapping
    public boolean save(@RequestBody Inventory inventory){
        System.out.println(inventory);
        return inventoryService.save(inventory);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Inventory inventory){
        return inventoryService.updateById(inventory);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return inventoryService.removeById(id);
    }
}

