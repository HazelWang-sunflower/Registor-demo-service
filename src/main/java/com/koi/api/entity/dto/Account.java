package com.koi.api.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.koi.api.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
@AllArgsConstructor
public class Account implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id; //包装类
    String username;
    String password;
    String email;
    String role;
    Date registerTime;//LocalDateTime
}
