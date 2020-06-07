package com.alibaba.nacos.example.spring.cloud.controller;


import com.alibaba.nacos.example.spring.cloud.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
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

    @Value("${provider.name:#{null}}")
    private String providerName;

    @RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
    @GlobalTransactional
    public String echo(@PathVariable String str, int num) {
        storageService.deduct(num);
        Assert.notNull(providerName, "请配置provider.name");
        return restTemplate.getForObject("http://" + providerName + "/echo/" + str + "?num=" + num, String.class);
    }

}
