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
    public Result save(@RequestBody User list){
        System.out.println(list);
        try {
            boolean res = userService.save(list);
            if(res) return Result.success();
            else return Result.fail();
        }catch (Exception e){
            return Result.fail();
        }
    }

    // 修改
    @PutMapping
    public Result modify(@RequestBody User user){
        boolean res =  userService.updateById(user);
        if(res) return Result.success();
        else return Result.fail();
    }

    @PostMapping("/listPage")
    public Result listByPage(@RequestBody QueryPageParam query) {
        try {
            // 获取参数
            long size = (long) query.getPageSize();
            long num = (long) query.getPageNum();
            String name = (String) query.getParams().get("name");
            String suid = (String) query.getParams().get("uid");
            Integer uid = null;
            if(suid.length() > 0){
                try {
                    uid = Integer.parseInt(suid);
                }catch (Exception e){ uid = -1;}
            }else uid = null;
            String phone = (String) query.getParams().get("phone");
            String sex = (String) query.getParams().get("selectSex");
            String startTime = (String) query.getParams().get("startTime");
            String endTime = (String) query.getParams().get("endTime");
            // 初始化分页信息
            Page<User> page = new Page<>();
            page.setSize(size);
            page.setCurrent(num);

            // 查询条件
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
            lqw.like(!name.equals("null") && StringUtils.isNoneBlank(name),User::getName,name);
            lqw.eq(uid != null,User::getUid,uid);
            lqw.like(!phone.equals("null") && StringUtils.isNoneBlank(phone), User::getPhone, phone);
            lqw.like(!phone.equals("null") && StringUtils.isNoneBlank(phone), User::getPhone, phone);
            lqw.eq(!sex.equals("null") && StringUtils.isNoneBlank(sex), User::getSex, sex);
            //if(StringUtils.isNoneBlank(startTime)){
            //    lqw.and(wrapper -> wrapper
            //            .likeRight(StringUtils.isNoneBlank(startTime), User::getDateOfEntry, startTime)
            //            .or()
            //            .likeRight(StringUtils.isNoneBlank(endTime), User::getDateOfEntry, endTime));
            //}
            //else{
            //    lqw.likeRight(StringUtils.isNoneBlank(endTime), User::getDateOfEntry, endTime);
            //}

            IPage iPage = userService.page(page, lqw);
            return Result.success(iPage.getRecords(),iPage.getTotal());
        }catch (Exception e){
            System.out.println("UserController.listByPage:" + e);
            return Result.fail(e.toString());
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        boolean res = userService.removeById(id);
        if(res) return Result.success();
        else return Result.fail();
    }
}

