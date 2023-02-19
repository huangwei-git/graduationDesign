package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.UserDao;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.bytecode.stackmap.BasicBlock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.function.Function;

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

    @Resource
    private UserDao userDao;

    @Override
    public Result pageList(QueryPageParam query) {
        try {
            System.out.println(query);
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

            String sortField = (String) query.getParams().get("sortField");
            String sortDirection = (String) query.getParams().get("sortDirection");

            String phone = (String) query.getParams().get("phone");
            String sjob = (String) query.getParams().get("job");
            Integer job = null;
            try {
                job = Integer.parseInt(sjob);
            }catch (Exception e){}

            String state = (String) query.getParams().get("state");
            String sex = (String) query.getParams().get("selectSex");
            Integer position = null;
            try {
                position = (Integer) query.getParams().get("position");
            }catch (Exception e){}

            String startTime = (String) query.getParams().get("startTime");
            String endTime = (String) query.getParams().get("endTime");
            // 初始化分页信息
            Page<User> page = new Page<>();
            page.setSize(size);
            page.setCurrent(num);
            // 查询条件
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
            if(!sortField.equals("null") && StringUtils.isNotBlank(sortField)){
                if(sortDirection.equals("1")){
                    if(sortField.equals("uid"))lqw.orderByDesc(User::getUid);
                    else if(sortField.equals("name"))lqw.orderByDesc(User::getName);
                    else if(sortField.equals("sex"))lqw.orderByDesc(User::getSex);
                    else if(sortField.equals("locSendId"))lqw.orderByDesc(User::getLocSendId);
                    else if(sortField.equals("phone"))lqw.orderByDesc(User::getPhone);
                    else if(sortField.equals("dateOfEntry"))lqw.orderByDesc(User::getDateOfEntry);
                    else if(sortField.equals("state"))lqw.orderByDesc(User::getState);
                    else if(sortField.equals("job"))lqw.orderByDesc(User::getJob);
                }else{
                    if(sortField.equals("uid"))lqw.orderByAsc(User::getUid);
                    else if(sortField.equals("name"))lqw.orderByAsc(User::getName);
                    else if(sortField.equals("sex"))lqw.orderByAsc(User::getSex);
                    else if(sortField.equals("locSendId"))lqw.orderByAsc(User::getLocSendId);
                    else if(sortField.equals("phone"))lqw.orderByAsc(User::getPhone);
                    else if(sortField.equals("dateOfEntry"))lqw.orderByAsc(User::getDateOfEntry);
                    else if(sortField.equals("state"))lqw.orderByAsc(User::getState);
                    else if(sortField.equals("job"))lqw.orderByAsc(User::getJob);
                }
            }
            lqw.eq(uid != null,User::getUid,uid);
            lqw.like(!name.equals("null") && StringUtils.isNoneBlank(name),User::getName,name);
            lqw.eq(!sex.equals("null") && StringUtils.isNoneBlank(sex), User::getSex, sex);
            lqw.like(!phone.equals("null") && StringUtils.isNoneBlank(phone), User::getPhone, phone);
            lqw.eq(state != null && StringUtils.isNotBlank(state),User::getState,state);
            lqw.eq(job != null,User::getJob,job);
            lqw.eq( position != null,User::getLocSendId,position);
            lqw.ge(startTime!= null && StringUtils.isNotBlank(startTime),User::getDateOfEntry,startTime);
            lqw.le(endTime!= null && StringUtils.isNotBlank(endTime),User::getDateOfEntry,endTime);
            IPage iPage = this.page(page, lqw);
            return Result.success(iPage.getRecords(),iPage.getTotal());
        }catch (Exception e){
            System.out.println("UserController.listByPage:" + e);
            return Result.fail(e.toString());
        }
    }
}
