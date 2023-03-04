package com.songlian.logistics.service;

import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.OrderForm;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface OrderFormService extends IService<OrderForm> {
    public Result pageList(QueryPageParam query);

    public Result genOrder(HashMap map);

    public Result genOrderOfTsp(HashMap map);

    public List<HashMap> getOrderBuffer(String key);
}
