package com.wang.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.cart.mapper.CartItemMapper;
import com.wang.gmall.service.CartItemService;
import com.wang.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 微笑
 * @date 2019/11/27 16:08
 */
@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    CartItemMapper cartItemMapper;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 判断当前用户购物车里面是否有这个skuId的商品
     * @param memberId
     * @param skuId
     * @return 有返回OmsCartItem，无：返回null
     */
    @Override
    public OmsCartItem ifCartExitByUser(String memberId, String skuId) {
        OmsCartItem omsCartItem=new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);

        return cartItemMapper.selectOne(omsCartItem);
    }

    /**
     * 添加到购物车
     * @param omsCartItem
     */
    @Override
    public void addCart(OmsCartItem omsCartItem) {
        /**
         * 如果有用户id则添加，避免空指针
         */
        String s=omsCartItem.getMemberId();
        if(StringUtils.isNotBlank(omsCartItem.getMemberId())) {
            cartItemMapper.insert(omsCartItem);
        }
    }

    /**
     * 修改购物车
     * @param omsCartItemFromDB
     */
    @Override
    public void updateCart(OmsCartItem omsCartItemFromDB) {
        Example example=new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id",omsCartItemFromDB.getId());
        cartItemMapper.updateByExample(omsCartItemFromDB,example);
    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItem omsCartItem=new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = cartItemMapper.select(omsCartItem);
        /**
         * 同步到redis
         */
        Jedis jedis= redisUtil.getJedis();
        Map<String,String> map=new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            /**
             * 计算一个sku商品的总价
             */
            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
            map.put(productSkuId, JSON.toJSONString(cartItem));
        }
        jedis.del("user:"+memberId+":cart");
        jedis.hmset("user:"+memberId+":cart",map);
        jedis.close();
    }

    /**
     * 从缓存中查询所有购物车商品
     * @param userId
     * @return
     */
    @Override
    public List<OmsCartItem> selectCartList(String userId) {
        Jedis jedis=null;
        List<OmsCartItem> omsCartItems=new ArrayList<>();
        try {
             jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + userId + ":cart");
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
            }

        }finally {
            jedis.close();
        }

        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example example=new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("productSkuId",omsCartItem.getProductSkuId()).andEqualTo("memberId",omsCartItem.getMemberId());
        cartItemMapper.updateByExampleSelective(omsCartItem,example);
        /**
         * 同步到redis缓存
         */
        flushCartCache(omsCartItem.getMemberId());
    }
}
