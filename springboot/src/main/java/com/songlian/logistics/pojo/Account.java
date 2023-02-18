package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Account {
    private static final long serialVersionUID = 1L;

    @TableId(value = "account_id",type = IdType.AUTO)
    private Integer accountId;

    private String password;
}
