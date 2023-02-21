package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.InventoryDao;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.service.InventoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryDao, Inventory> implements InventoryService {

    @Resource
    private InventoryDao inventoryDao;

    @Override
    public Result getStoreInfo(QueryPageParam query) {
        try {
            Integer pageSize = query.getPageSize();
            Integer pageNum = query.getPageNum();

            String inventoryName = (String) query.getParams().get("inventoryName");
            String materialName = (String) query.getParams().get("materialName");

            if(inventoryName.equals("null") || StringUtils.isBlank(inventoryName)) inventoryName = null;
            else inventoryName = "%" + inventoryName + "%";
            if(materialName.equals("null") || StringUtils.isBlank(materialName)) materialName = null;
            else materialName = "%" + materialName + "%";
            List<Map> map = inventoryDao.getStoreInfo(inventoryName, materialName, (pageNum - 1) * pageSize, pageSize);
            int total = inventoryDao.selectCount(null);
            return Result.success(map,total);
        }catch (Exception e){
            return Result.fail("服务器忙，请稍后重试");
        }
    }
}
