package com.songlian.logistics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.InventoryDao;
import com.songlian.logistics.dao.LocationDao;
import com.songlian.logistics.dao.MaterialDao;
import com.songlian.logistics.dao.UserDao;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.service.*;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@MapperScan("com.songlian.logistics.dao")
class SpringbootApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private MaterialDao materialDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private InventoryDao inventoryDao;

    public static int i = 0;

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
        List<Material> materials = materialService.list();

        LambdaQueryWrapper<Location> lqw1 = new LambdaQueryWrapper();
        lqw1.eq(Location::getType,0);
        List<Location> locations = locationDao.selectList(lqw1);

        locations.forEach(location -> {
            materials.forEach(material -> {
                LambdaQueryWrapper<Inventory> lqw2 = new LambdaQueryWrapper<>();
                lqw2.eq(Inventory::getMaterialId,material.getMaterialId());
                lqw2.eq(Inventory::getLocSendId,location.getLocId());
                if (inventoryDao.selectList(lqw2).size() == 0) {
                    Inventory inventory = new Inventory();
                    inventory.setLocSendId(location.getLocId());
                    inventory.setMaterialId(material.getMaterialId());
                    inventory.setAmount(0);
                    inventoryDao.insert(inventory);
                    //System.out.println(location.getName() + " ---> "+ material.getName());
                }
            });
        });
    }

    @Test
    public void testUpdateTime(){
    }

}
