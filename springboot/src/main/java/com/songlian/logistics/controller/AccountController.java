package com.songlian.logistics.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.exception.ServiceException;
import com.songlian.logistics.pojo.Account;
import com.songlian.logistics.service.AccountService;
import org.apache.commons.lang3.StringUtils;
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
 * @since 2023-02-18
 */
@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    // 新增
    @PostMapping
    public Result save(@RequestBody Account account){
        try {
            accountService.save(account);
            return Result.success();
        }catch (Exception e){
            System.out.println(e);
            return Result.fail("添加失败，请稍后重试");
        }
    }

    // 修改
    @PutMapping
    public Result modify(@RequestBody Account account) {
        try {
            accountService.updateById(account);
            return Result.success();
        }catch (Exception e){
            System.out.println(e);
            return Result.error(Constants.CODE_500,"修改失败，请稍后重试");
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try {
            accountService.removeById(id);
            return Result.success();
        }catch (Exception e){
            System.out.println(e);
            return Result.error(Constants.CODE_500,"删除失败，请稍后重试");
        }
    }

    // 登录验证
    @PostMapping("/login")
    public Result login(@RequestBody HashMap map) {
        try {
            return accountService.login(map);
        }catch (RequestExpcetion re){
            return Result.fail(re.getMessage());
        }catch (ServiceException se){
            System.out.println(se.getMessage());
            return Result.error(Constants.CODE_500,"登录失败，请稍后重试");
        }catch (Exception e){
            System.out.println("Account.Controll未知异常：" + e);
            return Result.error(Constants.CODE_600,"登录失败，请稍后重试");
        }
    }
}

