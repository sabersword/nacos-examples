package org.ypq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ypq.config.OutOfMoneyException;
import org.ypq.persistence.Account;
import org.ypq.persistence.AccountMapper;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Transactional
    public Account deduct(String userId, int num){
        Account account = accountMapper.selectByUserId(userId);
        account.setMoney(account.getMoney().subtract(new BigDecimal(num)));
        accountMapper.updateById(account);
        if (num < 0 || account.getMoney().compareTo(BigDecimal.ZERO) < 0) {
            throw new OutOfMoneyException("库存不足" + num + " " + account.getMoney());
        }
        return account;
    }

}
