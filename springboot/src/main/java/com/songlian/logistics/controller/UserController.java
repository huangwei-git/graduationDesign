package com.songlian.logistics.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 查询全部
    @GetMapping
    public Result list() {
        List<User> list = userService.list();

        List<User> data = userService.list();
        Result res = data == null ? Result.success(new ArrayList()) : Result.success(data, data.size());
        System.out.println(res);
        return res;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Integer id){
        User user = userService.getById(id);
        return Result.success(user,0);
    }


    @PostMapping
    public Result save(@RequestBody User user){
        try {
            System.out.println("user = " + user);
            userService.save(user);
            return Result.success();
        }catch (Exception e){
            System.out.println("User添加失败！" + e.getStackTrace());
            return Result.fail();
        }
    }

    // 修改
    @PutMapping
    public Result modify(@RequestBody User user){
        try {
            userService.updateById(user);
            return Result.success();
        }catch (Exception e){
            System.out.println("User更新失败！" + e.getStackTrace());
            return Result.fail();
        }
    }

    @PostMapping("/listPage")
    public Result listByPage(@RequestBody QueryPageParam query) {
        return userService.pageList(query);
    }

    // 删除
    @DeleteMapping("/{uid}")
    public Result delete(@PathVariable Integer uid) {
        try {
            userService.removeById(uid);
            return Result.success();
        }catch (Exception e){
            System.out.println("用户删除失败！" + e.getStackTrace());
            return Result.fail();
        }
    }
}

