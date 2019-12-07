package com.wang.gmall.order.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.bean.OmsOrder;
import com.wang.gmall.bean.OmsOrderItem;
import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.order.mapper.OrderItemMapper;
import com.wang.gmall.order.mapper.OrderMapper;
import com.wang.gmall.service.OrderService;
import com.wang.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

/**
 * @author 微笑
 * @date 2019/12/6 15:10
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Override
    public String genTradeCode(String memberId) {
        Jedis jedis=null;
        String tradeCode="";
        try{
            jedis=redisUtil.getJedis();
            String key="user:"+ memberId+":tradeCode";
            tradeCode= UUID.randomUUID().toString();
            jedis.setex(key,60*15,tradeCode);

        }finally {
            jedis.close();
        }


        return tradeCode;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis=null;
        try{
            jedis=redisUtil.getJedis();
            String key="user:"+ memberId+":tradeCode";
            String tradeCodeForCache = jedis.get(key);
            if(StringUtils.isNotBlank(tradeCodeForCache)&&tradeCodeForCache.equals(tradeCode)){
                jedis.del(key);
                return "success";
            }else{
                return "fail";
            }
        }finally {
            jedis.close();
        }
    }

    /**
     * 保存订单表
     * @param omsOrder
     */
    @Override
    public void saveOrder(OmsOrder omsOrder) {
        orderMapper.insertSelective(omsOrder);
        String orderId=omsOrder.getId();

        /**
         * 保存订单详情
         */
        List<OmsOrderItem> omsOrderItemList = omsOrder.getOmsOrderItemList();
        for (OmsOrderItem omsOrderItem : omsOrderItemList) {
            omsOrderItem.setOrderId(orderId);
            orderItemMapper.insertSelective(omsOrderItem);
            /**
             * 下一步删除购物车商品数据
             */
        }
    }
}
