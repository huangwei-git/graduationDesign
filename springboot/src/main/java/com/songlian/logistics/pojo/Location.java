package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "loc_id", type = IdType.AUTO)
    private Integer locId;

    private String name;

    private Integer type;

    private Double xpos;

    private Double ypos;

    @TableLogic(value = "0" ,delval = "1")
    private Integer deleted;
}
