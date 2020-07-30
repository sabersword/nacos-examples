package org.ypq.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ypq.persistence.Storage;
import org.ypq.persistence.StorageMapper;

@Service
public class StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Transactional
    public String deduct(String commodityCode, int count) {
        //There is a latent isolation problem here.
        //I hope that users can solve it and deepen their understanding of seata isolation.
        //At the bottom I will put a reference solution.
        Storage storage = storageMapper.findByCommodityCode(commodityCode);
        storage.setCount(storage.getCount() - count);
        storageMapper.updateCountById(storage);
        return "the rest of " + commodityCode + " is " + storage.getCount() + "   ";
    }

    @TwoPhaseBusinessAction(name = "orderTcc", commitMethod = "confirmDeduct", rollbackMethod = "cancelDeduct")
    @Transactional
    public String tryDeduct(BusinessActionContext actionContext,
                            @BusinessActionContextParameter(paramName = "commodityCode") String commodityCode,
                            @BusinessActionContextParameter(paramName = "count") int count) {
        Storage storage = storageMapper.findByCommodityCode(commodityCode);
        storage.setFreezeCount(storage.getFreezeCount() + count);
        storageMapper.updateFreezeById(storage);
        return "the rest of " + commodityCode + " is " + storage.getCount() + "   ";
    }

    @Transactional
    public String confirmDeduct(BusinessActionContext actionContext) {
        String commodityCode = (String) actionContext.getActionContext("commodityCode");
        int count = (Integer) actionContext.getActionContext("count");

        Storage storage = storageMapper.findByCommodityCode(commodityCode);
        storage.setFreezeCount(storage.getFreezeCount() - count);
        storage.setCount(storage.getCount() - count);
        storageMapper.updateFreezeAndCountById(storage);
        return "the rest of " + commodityCode + " is " + storage.getCount() + "   ";
    }

    @Transactional
    public String cancelDeduct(BusinessActionContext actionContext) {
        String commodityCode = (String) actionContext.getActionContext("commodityCode");
        int count = (Integer) actionContext.getActionContext("count");

        Storage storage = storageMapper.findByCommodityCode(commodityCode);
        storage.setFreezeCount(storage.getFreezeCount() - count);
        storageMapper.updateFreezeById(storage);
        return "the rest of " + commodityCode + " is " + storage.getCount() + "   ";
    }

}
