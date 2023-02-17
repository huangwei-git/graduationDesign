package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;
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
public class OrderForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    private Long orderId;

    @TableField("manager_id")
    private Integer managerId;

    @TableField("demand_id")
    private Integer demandId;

    @TableField("material_id")
    private Integer materialId;

    private Integer amount;

    @TableField("create_time")
    private LocalDateTime createTime;

    private LocalDateTime finishTime;


}
