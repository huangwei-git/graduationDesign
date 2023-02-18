package com.songlian.logistics.service.impl;

import com.songlian.logistics.pojo.User;
import com.songlian.logistics.dao.UserDao;
import com.songlian.logistics.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

}
