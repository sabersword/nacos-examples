package com.alibaba.nacos.example.spring.cloud.service;

import com.alibaba.nacos.example.spring.cloud.persistence.Storage;
import com.alibaba.nacos.example.spring.cloud.persistence.StorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Transactional
    public void deduct(int num) {
        Storage storage = storageMapper.findByCommodityCode("2001");
        storage.setCount(storage.getCount() - num);
        storageMapper.updateById(storage);
    }
}
