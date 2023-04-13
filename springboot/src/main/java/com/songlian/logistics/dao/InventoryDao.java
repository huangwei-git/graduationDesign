package com.songlian.logistics.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.songlian.logistics.pojo.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface InventoryDao extends BaseMapper<Inventory> {

    public List<Map> getStoreInfo(String inventoryName,
                                  String materialName,
                                  Integer pageStart,
                                  Integer pageSize);
}
