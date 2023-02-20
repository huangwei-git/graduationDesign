package com.songlian.logistics.controller;


import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
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
    public Result list() {
        try {
            List<Material> list = materialService.list();
            return Result.success(list,list.size());
        }catch (Exception e){
            return Result.fail("Material's list fail:" + e);
        }
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Integer id){
        return Result.success(materialService.getById(id),1);
    }

    // 新增
    @PostMapping
    public Result save(@RequestBody Material material) {
        try {
            System.out.println(material);
            materialService.save(material);
            return Result.success();
        }catch (Exception e){
            return Result.fail("Material's save fail:" + e);
        }
    }

    // 修改
    @PutMapping
    public Result modify(@RequestBody Material material){
        try {
            materialService.updateById(material);
            return Result.success();
        }catch (Exception e){
            return Result.error("Material's modify fail:" + e);
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        if(materialService.getById(id) == null){
            try {
                return Result.fail("该id不存在");
            }catch (Exception e){
                return Result.error("服务器忙，请稍后重试");
            }
        }else{
            try {
                materialService.removeById(id);
                return Result.success();
            }catch (Exception e){
                return Result.error("删除记录时出现错误:" + e);
            }
        }
    }

    // 分页查询1
    @PostMapping("/listPage")
    public Result list(@RequestBody QueryPageParam query){
        try {
            return materialService.pageList(query);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
}

