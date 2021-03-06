package com.alibaba.nacos.example.spring.cloud.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    Account selectByUserId(@Param("userId") String userId);

    int updateById(Account record);

}