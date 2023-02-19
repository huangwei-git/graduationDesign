package com.songlian.logistics.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Account;
import com.songlian.logistics.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // 查询全部
    @GetMapping
    public List<Account> list() {
        List<Account> list = accountService.list();
        list.forEach(account -> {
            System.out.println(account);
        });
        return list;
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Account selectById(@PathVariable Integer id){
            return accountService.getById(id);
    }

    // 新增
    @PostMapping
    public boolean save(@RequestBody Account account){
        System.out.println(account);
        return accountService.save(account);
    }

    // 修改
    @PutMapping
    public boolean modify(@RequestBody Account account){
        return accountService.updateById(account);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return accountService.removeById(id);
    }

    // 登录验证
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        return accountService.login(account);
    }
}

