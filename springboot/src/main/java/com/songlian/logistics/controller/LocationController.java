package com.songlian.logistics.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.exception.ServiceException;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.service.LocationService;
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
@RequestMapping("/location")
public class LocationController {
    @Autowired
    private LocationService locationService;
    
    // 查询全部
    @GetMapping
    public Result list() {
        List<Location> list = locationService.list();
        return Result.success(list,list.size());
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
    public Result save(@RequestBody Location location){
        try {
            LambdaQueryWrapper<Location> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Location::getXpos,location.getXpos());
            lqw.eq(Location::getYpos,location.getYpos());
            if(locationService.getOne(lqw) != null){
                return Result.error(Constants.CODE_401,"该坐标已存在！");
            }
            lqw.clear();
            lqw.eq(Location::getName,location.getName());
            if(locationService.getOne(lqw) != null){
                return Result.error(Constants.CODE_401,"地点名已存在！");
            }
            locationService.save(location);
            return Result.success();
        }catch (Exception e){
            return Result.serviceError("location save操作异常");
        }
    }

    // 修改
    @PutMapping
    public Result update(@RequestBody Location location){
        try {
            locationService.updateById(location);
            return Result.success();
        }catch (Exception e){
            return Result.serviceError("location update操作异常");
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try {
            if(id == null || id < 1){
                throw new RequestExpcetion(Constants.CODE_401,"locId参数错误");
            }else{
                if (locationService.getById(id) == null){
                    return Result.fail("该记录不存在！");
                }else{
                    locationService.removeById(id);
                    return Result.success();
                }
            }
        }catch (RequestExpcetion e) {
            return Result.requestError("location删除错误");
        }catch (Exception e){
            return Result.fail("location删除操作运行错误");
        }
    }

    @PostMapping("/listPage")
    public Result list(@RequestBody QueryPageParam query){
        try {
            return locationService.pageList(query);
        }catch (ServiceException e){
            return Result.serviceError(e.getMessage());
        }catch (RequestExpcetion e){
            return Result.requestError(e.getMessage());
        }
        catch (Exception e){
            return Result.fail("listPage运行错误");
        }
    }
}

