package com.songlian.logistics.controller;


import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public Result save(@RequestBody List<User> list){
        boolean res = userService.saveBatch(list);
        if(res) return Result.success();
        else return Result.fail();
    }

    // 修改
    @PutMapping
    public Result modify(@RequestBody User user){
        boolean res =  userService.updateById(user);
        if(res) return Result.success();
        else return Result.fail();
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        boolean res = userService.removeById(id);
        if(res) return Result.success();
        else return Result.fail();
    }
}

