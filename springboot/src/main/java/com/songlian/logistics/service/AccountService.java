package com.songlian.logistics.service;

import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-18
 */

public interface AccountService extends IService<Account> {
    public Result login(HashMap map);
}
