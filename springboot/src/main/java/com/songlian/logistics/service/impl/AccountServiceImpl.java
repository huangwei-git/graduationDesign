package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.AccountDao;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.exception.ServiceException;
import com.songlian.logistics.pojo.Account;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.AccountService;
import com.songlian.logistics.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {
    @Resource
    private UserService userService;

    @Override
    public Result login(HashMap map) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        try {
            LambdaQueryWrapper<Account> lqw = new LambdaQueryWrapper();
            lqw.eq(username != null && StringUtils.isNotBlank(username), Account::getUsername, username);
            Account account = this.getOne(lqw);
            if (account!= null) {
                if (account.getPassword().equals(password))
                    return Result.success(account);
                else
                throw new RequestExpcetion(Constants.CODE_401,"密码错误！");
            } else {
                throw new RequestExpcetion(Constants.CODE_401,"账号不存在！");
            }
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_600,"注册服务异常");
        }
    }
}
