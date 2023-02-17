package com.songlian.logistics;

import com.songlian.logistics.dao.DeliveryDao;
import com.songlian.logistics.pojo.Delivery;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan
class SpringbootApplicationTests {

    @Autowired
    DeliveryDao deliveryDao;

    @Test
    void contextLoads() {
        deliveryDao.selectList(null).forEach(item -> {
            System.out.println(item);
        });
    }

}
