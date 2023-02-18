package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.dao.AccountDao;
import com.songlian.logistics.pojo.Account;
import com.songlian.logistics.service.AccountService;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {
}
