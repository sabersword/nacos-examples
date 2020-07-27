package org.ypq.service;

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
        storageMapper.updateById(storage);
        return "the rest of " + commodityCode + " is " + storage.getCount() + "   ";
    }

}
