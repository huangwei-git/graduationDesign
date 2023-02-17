package com.songlian.logistics.controller;


import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.service.MaterialService;
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
@RequestMapping("/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    // 查询全部
    @GetMapping
    public List<Material> list() {
        List<Material> list = materialService.list();
        list.forEach(material -> {
            System.out.println(material);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Material selectById(@PathVariable Integer id){
        return materialService.getById(id);}

    // 新增
    @PostMapping
    public boolean save(@RequestBody Material material){
        return materialService.save(material);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Material material){
        return materialService.updateById(material);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return materialService.removeById(id);
    }
}

