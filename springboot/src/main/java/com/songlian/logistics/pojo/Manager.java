package com.songlian.logistics.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Manager implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "manager_id", type = IdType.AUTO)
    private Integer managerId;

    private String name;

    private String sex;

    private String email;

    private String phone;

    @TableField("date_of_entry")
    private LocalDateTime dateOfEntry;

    private String password;

    @Version
    private Integer deleted;


}
