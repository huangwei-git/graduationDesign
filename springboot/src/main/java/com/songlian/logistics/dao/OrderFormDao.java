package com.songlian.logistics.dao;

import com.songlian.logistics.pojo.OrderForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface OrderFormDao extends BaseMapper<OrderForm> {

    public List<HashMap> orderList(@Param("orderId") String orderId,
                                   @Param("materialName") String materialName,
                                   @Param("sortField") String sortField,
                                   @Param("sortDirection") String sortDirection,
                                   @Param("pageStart") Long pageStart,
                                   @Param("pageSize") Long pageSize
    );
}
