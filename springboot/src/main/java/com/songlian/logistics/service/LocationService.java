package com.songlian.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.pojo.Inventory;
import com.songlian.logistics.pojo.Location;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface LocationService extends IService<Location> {
    public Result pageList(QueryPageParam query) throws RequestExpcetion;
}
