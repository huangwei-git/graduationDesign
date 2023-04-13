package com.songlian.logistics.controller;


import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.MaterialDao;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.service.MaterialService;
import org.apache.ibatis.javassist.bytecode.ExceptionsAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private MaterialDao materialDao;

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
            return Result.serviceError("Material's modify fail:" + e);
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        if(materialService.getById(id) == null){
            try {
                return Result.fail("该id不存在");
            }catch (Exception e){
                return Result.serviceError("服务器忙，请稍后重试");
            }
        }else{
            try {
                materialService.removeById(id);
                return Result.success();
            }catch (Exception e){
                return Result.serviceError("删除记录时出现错误:" + e);
            }
        }
    }

    // 分页查询1
    @PostMapping("/listPage")
    public Result list(@RequestBody QueryPageParam query){
        try {
            return materialService.pageList(query);
        }catch (Exception e){
            return Result.serviceError(e.getMessage());
        }
    }

    @PostMapping("count")
    public Result listCount(@RequestBody QueryPageParam query) {

        try {
            return materialService.materialCount(query);
        }catch (Exception e){
            return Result.fail(e.toString());
        }
    }


    @GetMapping("piecharts")
    public Result piechartsData(){
        return materialService.materialCountOfPieCharts(null,null);
    }

    @GetMapping("getAllMaterialCount")
    public Result getAllMaterialCount(){
        try {
            List<Map> data = materialDao.getAllMaterialCount();
            return Result.success(data,data.size());
        }catch (Exception e){
            return Result.error(500,"服务器异常,获取库存材料信息失败");
        }
    }

    @PostMapping("getSelectMaterialCount")
    public Result getSelectMaterialCount(@RequestBody Map map){
        try {
            Integer locId = (Integer) map.get("locId");
            List<Map> data = materialDao.getSelectMaterialCount(locId);
            return Result.success(data,data.size());
        }catch (Exception e){
            System.out.println(e);
            return Result.error(500,"服务器异常,获取库存材料信息失败");
        }
    }

    // 导出
    @GetMapping("/export")
    public void exportData(HttpServletResponse response) throws Exception {
        materialService.exportData(response);
    }
}

