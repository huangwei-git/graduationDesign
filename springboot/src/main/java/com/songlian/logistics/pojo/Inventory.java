package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.*;
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
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "store_id",type = IdType.AUTO)
    private Integer storeId;

    @TableField("loc_send_id")
    private Integer locSendId;

    @TableField("material_id")
    private Integer materialId;

    private Integer amount;

    @TableLogic(value = "0" ,delval = "1")
    private Integer deleted;

}
