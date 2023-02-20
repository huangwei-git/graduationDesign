package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.AccountDao;
import com.songlian.logistics.exception.ServiceException;
import com.songlian.logistics.pojo.Account;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.AccountService;
import com.songlian.logistics.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {
    @Resource
    private UserService userService;

    @Override
    public Result login(Account account) {
        System.out.println(account);
        try {
            LambdaQueryWrapper<Account> lqw = new LambdaQueryWrapper();
            lqw.eq(account.getUid() != null, Account::getUid, account.getUid());
            lqw.eq(account.getPassword() != null, Account::getPassword, account.getPassword());
            if (this.getOne(lqw) != null) {
                System.out.println("?");
                User user = userService.getById(account.getUid());
                return Result.success(user);
            } else if (this.getById(account.getUid()) != null) {
                throw new ServiceException(Constants.CODE_600,"用户密码错误！");
            } else {
                throw new ServiceException(Constants.CODE_600,"账号不存在！");
            }
        } catch (Exception e) {
            System.out.println("用户登录是失败！" + e.getMessage());
            return Result.fail("服务器忙，请稍后重试");
        }
    }
}
