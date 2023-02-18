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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    private String name;

    private String sex;

    private String email;

    private String phone;

    private Integer post;

    @TableField("date_of_entry")
    private LocalDateTime dateOfEntry;

    @TableField("loc_Send_id")
    private Integer locSendId;

    private String state;

    @TableLogic(value = "0" ,delval = "1")
    private Integer deleted;


}
