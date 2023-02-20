package com.songlian.logistics.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.songlian.logistics.pojo.Material;
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
public interface MaterialDao extends BaseMapper<Material> {

    @Select("SELECT\n" +
            "            mat.material_id as materialId,\n" +
            "            mat.`name` as materialName,\n" +
            "            SUM(inv.amount) as materialCount\n" +
            "        FROM `inventory` inv\n" +
            "                 LEFT JOIN  `material` mat\n" +
            "                            ON inv.material_id=mat.material_id\n" +
            "        WHERE inv.deleted=0\n" +
            "        GROUP BY(inv.material_id);")
    public List<HashMap> getMaterialCount();

    @Select("select * from material where material_id=1;")
    public Material getOne();
}
