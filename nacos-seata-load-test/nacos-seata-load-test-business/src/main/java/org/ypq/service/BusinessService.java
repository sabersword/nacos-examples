package org.ypq.service;

import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BusinessService {

    @Autowired
    private RestTemplate restTemplate;

    @GlobalTransactional
    public String purchase(String userId, String commodityCode, int orderCount) {
        String result = restTemplate.getForObject("http://storage-service/deduct?commodityCode=" + commodityCode + "&num=" + orderCount, String.class);
        result += restTemplate.getForObject("http://order-service/create?userId=" + userId + "&commodityCode=" + commodityCode + "&num=" + orderCount, String.class);
        if (result.contains("not enough")) {
            throw new RuntimeException("Business out of money");
        }
        return result;
    }

    @GlobalTransactional
    public String purchaseTCC(String userId, String commodityCode, int orderCount) {
        String result = restTemplate.getForObject("http://storage-service/tryDeduct?commodityCode=" + commodityCode + "&num=" + orderCount, String.class);
        result += restTemplate.getForObject("http://order-service/tryCreate?userId=" + userId + "&commodityCode=" + commodityCode + "&num=" + orderCount, String.class);
        if (result.contains("try failed")) {
            throw new RuntimeException("the try failed!!!");
        }
        return result;
    }
}
