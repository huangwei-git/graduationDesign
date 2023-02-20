package com.songlian.logistics.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.songlian.logistics.pojo.Location;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface LocationDao extends BaseMapper<Location> {

    @Autowired
    public Location testloc();

}
