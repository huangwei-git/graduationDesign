package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
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
public class OrderForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    private Long orderId;

    @TableField("manager_id")
    private Integer managerId;

    @TableField("loc_send_id")
    private Integer locSendId;

    @TableField("material_id")
    private Integer materialId;

    private Integer amount;

    @TableField("create_date")
    private LocalDateTime createDate;

    private LocalDateTime finishDate;

    @TableLogic(value = "0" ,delval = "1")
    private Integer deleted;
}

//{
//        "managerId": 1,
//        "locationId": 1,
//        "materialId": 1,
//        "amount":10
//        }