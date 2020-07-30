package org.ypq.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        accountMapper.updateMoneyById(account);
        if (num < 0 || account.getMoney().compareTo(BigDecimal.ZERO) < 0) {
            return null;
        }
        return account;
    }

    @TwoPhaseBusinessAction(name = "accountTcc", commitMethod = "confirmDeduct", rollbackMethod = "cancelDeduct")
    @Transactional
    public Account tryDeduct(BusinessActionContext actionContext,
                             @BusinessActionContextParameter(paramName = "userId") String userId,
                             @BusinessActionContextParameter(paramName = "num") int num){
        Account account = accountMapper.selectByUserId(userId);
        // freeze + num > money ,余额不足以冻结
        if (account.getFreezeMoney().add(new BigDecimal(num)).compareTo(account.getMoney()) > 0) {
            return null;
        }
        account.setFreezeMoney(account.getFreezeMoney().add(new BigDecimal(num)));
        return account;
    }

    @Transactional
    public Account confirmDeduct(BusinessActionContext actionContext){
        String userId = (String) actionContext.getActionContext("userId");
        int num = (Integer) actionContext.getActionContext("num");
        Account account = accountMapper.selectByUserId(userId);
        account.setFreezeMoney(account.getFreezeMoney().subtract(new BigDecimal(num)));
        account.setMoney(account.getMoney().subtract(new BigDecimal(num)));
        accountMapper.updateFreezeAndMoneyById(account);
        return account;
    }

    @Transactional
    public Account cancelDeduct(BusinessActionContext actionContext){
        String userId = (String) actionContext.getActionContext("userId");
        int num = (Integer) actionContext.getActionContext("num");
        Account account = accountMapper.selectByUserId(userId);
        account.setFreezeMoney(account.getFreezeMoney().subtract(new BigDecimal(num)));
        accountMapper.updateFreezeById(account);
        return account;
    }

}
