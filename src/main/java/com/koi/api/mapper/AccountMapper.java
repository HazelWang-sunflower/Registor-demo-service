package com.koi.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.koi.api.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
