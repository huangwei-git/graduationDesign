package com.songlian.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Inventory;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface InventoryService extends IService<Inventory> {

    public Result getStoreInfo(QueryPageParam query);


}
