package com.songlian.logistics.dao;

import com.songlian.logistics.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
public interface UserDao extends BaseMapper<User> {
    public List<HashMap> exportUserInfo();

    @Select("SELECT \n" +
            "\tloc.`name` as locName, \n" +
            "\tCOUNT(*) as num \n" +
            "FROM `location` loc \n" +
            "RIGHT JOIN `user` user \n" +
            "ON \n" +
            "\tuser.loc_send_id = loc.loc_id\n" +
            "WHERE\n" +
            "\tuser.loc_send_id != 0\n" +
            "\tAND\n" +
            "\tuser.state = #{state}\n" +
            "GROUP BY\n" +
            "\tuser.loc_send_id;")
    public List<HashMap> getNum(@Param("state") String state);

    @Select("SELECT count(*) AS num FROM user WHERE sex = #{sex};")
    public Integer getSexNum(@Param("sex") String sex);
}
