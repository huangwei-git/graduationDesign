package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "material_id", type = IdType.AUTO)
    private Integer materialId;

    private String name;

    private Integer cost;

    private Integer price;

    private Double occupy;

    @Version
    private Integer deleted;


}
