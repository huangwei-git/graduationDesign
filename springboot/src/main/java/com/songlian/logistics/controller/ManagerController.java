package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.Manager;
import com.songlian.logistics.service.ManagerService;
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
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    // 查询全部
    @GetMapping
    public List<Manager> list() {
        List<Manager> list = managerService.list();
        list.forEach(manager -> {
            System.out.println(manager);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Manager selectById(@PathVariable Integer id){
        return managerService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody Manager manager){
        return managerService.save(manager);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Manager manager){
        return managerService.updateById(manager);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return managerService.removeById(id);
    }
}

