package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

    // 订单ID
    @TableField("order_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    // 从哪运
    @TableField("supplier_loc_id")
    private Integer supplierLocId;

    // 运去哪
    @TableField("demander_loc_id")
    private Integer demanderLocId;

    // 运输量
    private Integer amount;

    // 运费
    private Integer toll;

    // 运输顺序
    private Integer seq;

    @TableLogic(value = "0" ,delval = "1")
    private Integer deleted;
}
