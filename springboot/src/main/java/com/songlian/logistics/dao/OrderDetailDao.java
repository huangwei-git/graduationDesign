package com.songlian.logistics.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.songlian.logistics.pojo.OrderDetail;
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
public interface OrderDetailDao extends BaseMapper<OrderDetail> {

    public List<HashMap> orderDetailList(@Param("orderId") String orderId,
                                         @Param("materialName") String materialName,
                                         @Param("supplierName") String supplierName,
                                         @Param("demanderName") String demanderName,
                                         @Param("sortField") String sortField,
                                         @Param("sortDirection") String sortDirection,
                                         @Param("pageStart") Long pageStart,
                                         @Param("pageSize") Long pageSize);
}
