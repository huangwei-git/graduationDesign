package com.songlian.logistics.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/location")
public class LocationController {
    @Autowired
    private LocationService locationService;
    
    // 查询全部
    @GetMapping
    public List<Location> list() {
        List<Location> list = locationService.list();
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Location selectById(@PathVariable Integer id){
        return locationService.getById(id);
    }

    @GetMapping("/type")
    public Result selectByType(@RequestParam Integer type) {
        LambdaQueryWrapper<Location> lqw = new LambdaQueryWrapper();
        lqw.eq(type != null, Location::getType, type);
        lqw.select(Location::getName,Location::getLocId);
        List<Location> data = locationService.list(lqw);
        return Result.success(data, data.size());
    }

    // 新增
    @PostMapping
    public boolean save(@RequestBody Location location){
        return locationService.save(location);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Location location){
        return locationService.updateById(location);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return locationService.removeById(id);
    }
}

