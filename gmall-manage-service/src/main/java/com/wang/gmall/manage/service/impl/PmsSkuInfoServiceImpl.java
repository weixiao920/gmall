package com.wang.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.bean.*;
import com.wang.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.wang.gmall.manage.mapper.PmsSkuImageMapper;
import com.wang.gmall.manage.mapper.PmsSkuInfoMapper;
import com.wang.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.wang.gmall.service.PmsSkuInfoService;
import com.wang.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

/**
 * @author 微笑
 * @date 2019/11/15 22:13
 */
@Service
/**
 * 保存SKU
 */
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    RedisUtil redisUtil;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfoMapper.insert(pmsSkuInfo);
        String productId=pmsSkuInfo.getProductId();

        /**
         * 保存图片
         */
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for(PmsSkuImage pmsSkuImage:skuImageList){
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        /**
         * 保存平台属性
         */
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for(PmsSkuAttrValue pmsSkuAttrValue:skuAttrValueList){
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }

        /**
         * 保存销售属性
         */
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue:skuSaleAttrValueList){
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
    }

    @Override
    public PmsSkuInfo getSkuInfo(String skuId) {
        PmsSkuInfo pmsSkuInfo;
        /**
         * 连接缓存
         */
        Jedis jedis = redisUtil.getJedis();
        /**
         * 查询缓存
         * 定义key的类型:key:  "sku:"+skuId+":info";
         */
        String skuKey="sku:"+skuId+":info";
        String skuJson = jedis.get(skuKey);

        if(StringUtils.isNotBlank(skuJson)){
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }else{
            /**
             * 防止缓存击穿
             * 设置分布式锁  如果访问为null,设置一个key ,10秒过期策略，才能继续访问DB
             * toValue:设置一个唯一值作为value,用来判断key属于那条线程
             */
            String toValue = UUID.randomUUID().toString();
            String OK = jedis.set("sku:" + skuId + ":lock", toValue, "nx", "px", 10*1000);
            if(StringUtils.isNotBlank(OK)&& OK.equals("OK")){
                /**
                 * 如果缓存中对应的skuId没有,则查询MySQL数据库
                 */
                pmsSkuInfo = getSkuInfoToDB(skuId);
                /**
                 * 如果数据库查到存入redis
                 */
                if(pmsSkuInfo!=null){
                    jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                }else{
                    /**
                     * 缓存没有这个key,DB里面也没有,高并发情况下就会一直访问DB
                     * 为了防止缓存穿透将，null或者空字符串值设置给redis,设置一个3分钟过期策略
                     */
                    jedis.setex("sku:" + skuId + ":info", 3*60,JSON.toJSONString(""));
                }
                /**
                 * 查询完MySql,要释放MySql分布锁
                 * 如果这条线程的锁过期了，这条线程还没走到这里,那么其他线程来会重新设置key,当前线程就会删掉其他线程的key
                 * 所以用Value值判断这个key属于那条线程,
                 */
                String lockValue = jedis.get("sku:" + skuId + ":info");
                if(StringUtils.isNotBlank(lockValue) && lockValue.equals(toValue)) {
                    jedis.del("sku:" + skuId + ":lock");
                }

            }else{
                /**
                 * 设置失败,自旋,(该线程睡眠几秒后，重新尝试访问本方法)
                 */
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfo(skuId);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    public PmsSkuInfo getSkuInfoToDB(String skuId) {
        PmsSkuInfo pmsSkuInfo=new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        /**
         * 获取图片集合
         */
        PmsSkuImage pmsSkuImage=new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);

        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);

        return skuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuAttrValueListBySpu(String id) {
        return pmsSkuInfoMapper.selectSkuAttrValueListBySpu(id);
    }

    /**
     * 获取所有商品sku属性
     * @return
     */
    @Override
    public List<PmsSkuInfo> getSkuAll() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for(PmsSkuInfo pmsSkuInfo:pmsSkuInfos){
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue=new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);

        }

        return pmsSkuInfos;
    }


}
