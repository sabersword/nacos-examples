package org.ypq.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.ypq.persistence.Order;
import org.ypq.persistence.OrderMapper;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public String create(String userId, String commodityCode, Integer count){
        BigDecimal orderMoney = new BigDecimal(count);
        Order order = new Order();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(count);
        order.setMoney(orderMoney);
        orderMapper.insert(order);
        String result = "order is [userId:" + userId + "],[commodityCode:" + commodityCode +"],[count:" + count +"]    ";
        result += restTemplate.getForObject("http://account-service/deduct?userId=" + userId + "&num=" + count, String.class);
        return result;
    }

    @TwoPhaseBusinessAction(name = "orderTcc", commitMethod = "confirmCreate", rollbackMethod = "cancelCreate")
    @Transactional
    public String tryCreate(BusinessActionContext actionContext,
                            @BusinessActionContextParameter(paramName = "userId") String userId,
                            @BusinessActionContextParameter(paramName = "commodityCode") String commodityCode,
                            @BusinessActionContextParameter(paramName = "count") Integer count,
                            @BusinessActionContextParameter(paramName = "orderIdMap") Map<String, Integer> orderIdMap){
        BigDecimal orderMoney = new BigDecimal(count);
        Order order = new Order();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(count);
        order.setMoney(orderMoney);
        orderMapper.insert(order);
        orderIdMap.put("orderId", order.getId());
        String result = order.getId() + "|";
        result += restTemplate.getForObject("http://account-service/tryDeduct?userId=" + userId + "&num=" + count, String.class);
        return result;
    }

    @Transactional
    public String confirmCreate(BusinessActionContext actionContext){
        Map<String, Integer> orderIdMap = (Map<String, Integer>)actionContext.getActionContext("orderIdMap");
        Order order = new Order();
        order.setId(orderIdMap.get("orderId"));
        order.setStatus("NORMAL");
        orderMapper.updateNormal(order);
        return null;
    }

    @Transactional
    public String cancelCreate(BusinessActionContext actionContext){
        Map<String, Integer> orderIdMap = (Map<String, Integer>)actionContext.getActionContext("orderIdMap");
        Order order = new Order();
        order.setId(orderIdMap.get("orderId"));
        orderMapper.delete(order);
        return null;
    }

}
