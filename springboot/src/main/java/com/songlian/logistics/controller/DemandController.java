package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.Demand;
import com.songlian.logistics.service.DemandService;
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
@RequestMapping("/demand")
public class DemandController {
    @Autowired
    private DemandService demandService;
    
    // 查询全部
    @GetMapping
    public List<Demand> list() {
        List<Demand> list = demandService.list();
        list.forEach(demand -> {
            System.out.println(demand);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Demand selectById(@PathVariable Integer id){
        return demandService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody Demand demand){
        return demandService.save(demand);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Demand demand){
        return demandService.updateById(demand);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return demandService.removeById(id);
    }
}

