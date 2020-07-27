package org.ypq.controller;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BusinessController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 减库存，下订单
     *
     * @param userId
     * @param commodityCode
     * @param orderCount
     */
    @GlobalTransactional
    @GetMapping(value = "/business")
    public String purchase(String userId, String commodityCode, int orderCount) {
        LOGGER.info("purchase begin ... xid: " + RootContext.getXID());
        String result = restTemplate.getForObject("http://storage-service/deduct?commodityCode=" + commodityCode + "&num=" + orderCount, String.class);
        result += restTemplate.getForObject("http://order-service/create?userId=" + userId + "&commodityCode=" + commodityCode + "&num=" + orderCount, String.class);
        if (result.contains("库存不足")) {
            throw new RuntimeException("Business out of storage");
        }
        return result;
    }

}
