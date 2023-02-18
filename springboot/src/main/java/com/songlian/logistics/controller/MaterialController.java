package com.songlian.logistics.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result selectById(@PathVariable Integer id){
        return Result.success(materialService.getById(id),1);
    }

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

    // 分页查询1
    @PostMapping("/listPage")
    public List<Material> list(@RequestBody QueryPageParam query){
        Page<Material> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Material> lqw = new LambdaQueryWrapper<>();
        String name = (String) query.getParam().get("name");
        lqw.like(Material::getName,name);

        IPage iPage = materialService.page(page, lqw);

        System.out.println(iPage.getTotal());

        return iPage.getRecords();
    }

    // 分页查询2: 在Service端创建Ipage
    @PostMapping("listPageByService")
    public List<Material> list2(@RequestBody QueryPageParam query){
        Page<Material> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        IPage iPage = materialService.genPage(page);

        System.out.println(iPage.getTotal());

        return iPage.getRecords();
    }
}

