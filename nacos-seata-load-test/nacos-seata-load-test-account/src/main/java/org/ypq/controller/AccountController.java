package org.ypq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ypq.config.OutOfMoneyException;
import org.ypq.persistence.Account;
import org.ypq.service.AccountService;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "/deduct")
    public String deduct(String userId, int num) {
        Account account = null;
        try {
            account = accountService.deduct(userId, num);
        } catch (OutOfMoneyException e) {
            return e.getMessage();
        }
        return userId + "'s balance is " + account.getMoney();
    }

}
