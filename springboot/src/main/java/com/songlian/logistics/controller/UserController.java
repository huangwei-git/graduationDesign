package com.songlian.logistics.controller;


import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.net.URLEncoder;
import java.time.LocalDateTime;
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

    // 导入
    @PostMapping("/import")
    public void importData(MultipartFile file) throws Exception {
        userService.importData(file);
    }

    // 导出
    @GetMapping("/export")
    public void exportData(HttpServletResponse response) throws Exception {
        userService.exportData(response);
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
        System.out.println("user = " + user);
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

    @GetMapping("/getCountData")
    public Result userCountData(){
        return userService.userCountData();
    }

    @GetMapping("/getCountSexRate")
    public Result sexCountData(){
        return userService.userSexCount();
    }

    @GetMapping("getMonthData")
    public Result getMonthData(){
        int months[] = new int[12];
        userService.list().forEach(user->{
            LocalDateTime date = user.getDateOfEntry();
            int x = date.getMonth().getValue();
            months[x - 1]++;
        });
        return Result.success(months);
    }
}

