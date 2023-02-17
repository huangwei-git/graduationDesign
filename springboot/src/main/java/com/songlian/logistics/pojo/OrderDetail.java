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
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "detail_id",type = IdType.AUTO)
    private Integer detailId;

    @TableField("order_id")
    private Long orderId;

    @TableField("transporter_id")
    private Integer transporterId;

    @TableField("loc_receive_id")
    private Integer locReceiveId;

    private Integer amount;

    @TableLogic(value = "0" ,delval = "1")
    private Integer deleted;
}
