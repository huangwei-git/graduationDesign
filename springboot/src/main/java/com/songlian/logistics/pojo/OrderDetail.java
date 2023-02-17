package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
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
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @MppMultiId
    @TableField("order_id")
    private Long orderId;

    @MppMultiId
    @TableField("transporter_id")
    private Integer transporterId;

    @MppMultiId
    @TableField("delivery_id")
    private Integer deliveryId;

    private Integer amount;


}
