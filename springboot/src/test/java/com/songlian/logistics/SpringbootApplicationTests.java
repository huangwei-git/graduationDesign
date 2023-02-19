package com.songlian.logistics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Account;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.AccountService;
import com.songlian.logistics.service.UserService;
import com.songlian.logistics.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@SpringBootTest
@MapperScan("com.songlian.logistics.dao")
class SpringbootApplicationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    @Test
    void contextLoads() {
        QueryPageParam query = new QueryPageParam();
        query.setPageNum(1);
        query.setPageSize(5);
        HashMap map = new HashMap();
        map.put("name","");
        map.put("uid","");
        map.put("phone","");
        map.put("selectSex","");
        map.put("startTime","");
        map.put("endTime","");
        query.setParams(map);
        Result data = userService.pageList(query);
        System.out.println(data.getCode());
        System.out.println(data.getTotal());
        String rcds[] = data.getData().toString().split("User");
        for(String rcd : rcds){
            System.out.println(rcd);
        }
    }

    @Test
    public void TestJoin(){
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
        Page page = new Page();
        page.setCurrent(1);
        page.setSize(20L);
        Date date = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            date = format.parse("2023-02-19 00:00");
            System.out.println(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        lqw.lt(User::getDateOfEntry, "2023-02-19T00:00:00");
        IPage iPage = userService.page(page,lqw);
        iPage.getRecords().forEach(item -> {
            System.out.println(item);
        });
    }

    @Test
    public void testAccountLogin(){
            userService.getById(1);
    }

}
