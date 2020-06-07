package com.alibaba.nacos.example.spring.cloud.controller;


import com.alibaba.nacos.example.spring.cloud.persistence.Storage;
import com.alibaba.nacos.example.spring.cloud.persistence.StorageMapper;
import com.alibaba.nacos.example.spring.cloud.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class StorageController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
    @GlobalTransactional
    public String echo(@PathVariable String str, int num) {
        storageService.deduct(num);
        return restTemplate.getForObject("http://service-provider/echo/" + str + "?num=" + num, String.class);
    }

}
