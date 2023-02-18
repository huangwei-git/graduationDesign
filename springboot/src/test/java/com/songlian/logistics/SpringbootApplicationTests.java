package com.songlian.logistics;

import com.songlian.logistics.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan
class SpringbootApplicationTests {

    @Autowired
    UserDao deliveryDao;

    @Test
    void contextLoads() {

    }

}
