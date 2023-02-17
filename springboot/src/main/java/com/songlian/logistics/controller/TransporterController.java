package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.Transporter;
import com.songlian.logistics.service.TransporterService;
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
@RequestMapping("/transporter")
public class TransporterController {
    @Autowired
    private TransporterService transporterService;

    // 查询全部
    @GetMapping
    public List<Transporter> list() {
        List<Transporter> list = transporterService.list();
        list.forEach(transporter -> {
            System.out.println(transporter);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Transporter selectById(@PathVariable Integer id){
        return transporterService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody Transporter transporter){
        return transporterService.save(transporter);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Transporter transporter){
        return transporterService.updateById(transporter);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return transporterService.removeById(id);
    }
}

