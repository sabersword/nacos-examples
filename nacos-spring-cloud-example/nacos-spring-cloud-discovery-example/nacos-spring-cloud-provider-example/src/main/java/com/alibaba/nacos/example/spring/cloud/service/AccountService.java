package com.alibaba.nacos.example.spring.cloud.service;

import com.alibaba.nacos.example.spring.cloud.persistence.Account;
import com.alibaba.nacos.example.spring.cloud.persistence.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Transactional
    public Account deduct(int num){
        Account account = accountMapper.selectByUserId("1001");
        account.setMoney(account.getMoney().subtract(new BigDecimal(num)));
        accountMapper.updateById(account);
        return account;
    }

}
