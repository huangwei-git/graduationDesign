package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.dao.InventoryDao;
import com.songlian.logistics.dao.LocationDao;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.service.InventoryService;
import com.songlian.logistics.service.LocationService;
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
public class LocationServiceImpl extends ServiceImpl<LocationDao, Location> implements LocationService {
}
