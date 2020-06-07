package com.alibaba.nacos.example.spring.cloud.controller;

import com.alibaba.nacos.example.spring.cloud.persistence.Account;
import com.alibaba.nacos.example.spring.cloud.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/echo/{string}", method = RequestMethod.GET)
    public String echo(@PathVariable String string, int num) {
        Account account = accountService.deduct(num);
        if (account.getMoney().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("库存不足");
        }
        return "Hello Nacos Discovery " + string;
    }
}
