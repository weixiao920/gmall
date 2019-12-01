package com.wang.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.service.CartItemService;
import com.wang.gmall.service.PmsSkuInfoService;
import com.wang.gmall.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/27 9:27
 */
@Controller
public class CartController {

    @Reference
    PmsSkuInfoService pmsSkuInfoService;

    @Reference
    CartItemService cartItemService;

    /**
     * 返回结算页面
     * @param request
     * @param response
     * @param modelMap
     * @return
     */
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        return "toTrade";
    }
    /**
     * 异步刷新购物车商品
     */
    @RequestMapping("checkCart")
    public String checkCart(String skuId, String isChecked,ModelMap modelMap ){
        OmsCartItem omsCartItem=new OmsCartItem();
        omsCartItem.setMemberId("1");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        /**
         * 修改状态值
         */
        cartItemService.checkCart(omsCartItem);
        /**
         * 从缓存查出最新值，渲染给页面
         */
        List<OmsCartItem> omsCartItems = cartItemService.selectCartList("1");
        modelMap.put("cartList",omsCartItems);
        /**
         * 计算结算时，被勾选的总价
         */
        BigDecimal sumPrice=gerSumPrice(omsCartItems);
        modelMap.put("sumPrice",sumPrice);
        return "cartListInner";
    }

    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        List<OmsCartItem> omsCartItems=new ArrayList<>();

        String userId="1";
        if(StringUtils.isNotBlank(userId)){
            /**
             * userId不为空 登录状态 查询db
             */
            omsCartItems=cartItemService.selectCartList(userId);
        }else{
            /**
             * userId为空 未登录状态 查询cookie
             */
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }
        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));

        }
        modelMap.put("cartList",omsCartItems);
        modelMap.put("userId",userId);
        /**
         * 计算结算时，被勾选的总价
         */
        BigDecimal sumPrice=gerSumPrice(omsCartItems);
        modelMap.put("sumPrice",sumPrice);
        return "cartList";
    }

    private BigDecimal gerSumPrice(List<OmsCartItem> omsCartItems) {
        BigDecimal bigDecimal=new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();
            if(omsCartItem.getIsChecked().equals("1")){
                bigDecimal=bigDecimal.add(totalPrice);
            }

        }
        return bigDecimal;
    }

    @RequestMapping("addToCart")
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response){
        List<OmsCartItem> omsCartItems=new ArrayList<>();
        /**
         * 获取商品sku信息
         */
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.getSkuInfo(skuId);

        /**
         * 将商品sku信息封装到OmsCartItem
         */
        OmsCartItem omsCartItem=new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        /**
         * 将购物车信息存储到cookie/DB
         *      1判断用户是否登录
         *          1.1登录状态 ->DB
         *
         *          1.2未登录状态 ->cookie
         *              1.2.1 存在cookie
         *                  1.2.1.1 cookie存在数据是否有新增数据
         *             1.2.2 不存在cookie
         *
         *
         */
        String memberId="1";
        if(StringUtils.isBlank(memberId)){
            /**
             *  1.2未登录状态 ->cookie
             */
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                /**
                 * 存在cookie
                 *  判断原有cookie数据与是否有新增数据
                 */
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                boolean exit=if_cart_exit(omsCartItems,omsCartItem);
                if(exit){
                    /**
                     * 存在新增数据
                     */
                    for (OmsCartItem cartItem : omsCartItems) {
                        cartItem.setQuantity(cartItem.getQuantity().add(new BigDecimal(quantity)));
                    }
                }else{
                    /**
                     * 不存在新增数据
                     */
                    omsCartItems.add(omsCartItem);
                }
            }else{
                /**
                 * 不存在cookie
                 */
                omsCartItems.add(omsCartItem);
            }
            /**
             * 更新cookie
             */
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(omsCartItems),60*60*24,true );
        }else{
            /**
             *  1.1登录状态 ->DB
             *      新增购物车数据是否在DB存在
             */
            OmsCartItem omsCartItemFromDB=cartItemService.ifCartExitByUser(memberId,skuId);
            if(omsCartItemFromDB==null){
                /**
                 * 不存在，新增操作
                 */
                omsCartItem.setMemberId(memberId);
                cartItemService.addCart(omsCartItem);
            }else{
                /**
                 * 存在，更新操作
                 */
                omsCartItemFromDB.setQuantity(omsCartItemFromDB.getQuantity().add(new BigDecimal(quantity)));
                cartItemService.updateCart(omsCartItemFromDB);

                /**
                 * 同步到redis缓存中
                 */
                cartItemService.flushCartCache(memberId);
            }



        }
        return "redirect:/success.html";
    }

    private boolean if_cart_exit(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean flag=false;
        for (OmsCartItem cartItem : omsCartItems) {
            if(omsCartItem.getProductSkuId().equals(cartItem.getProductSkuId())){
                flag=true;
            }
        }
        return flag;
    }
}
