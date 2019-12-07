package com.wang.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wang.gmall.annotations.LoginRequired;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.bean.OmsOrder;
import com.wang.gmall.bean.OmsOrderItem;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.service.CartItemService;
import com.wang.gmall.service.OrderService;
import com.wang.gmall.service.PmsSkuInfoService;
import com.wang.gmall.service.UmsMemberService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 微笑
 * @date 2019/12/5 20:49
 */
@Controller
public class OrderController {
    @Reference
    CartItemService cartItemService;

    @Reference
    UmsMemberService umsMemberService;

    @Reference
    OrderService orderService;

    @Reference
    PmsSkuInfoService pmsSkuInfoService;

    /**
     * 返回结算页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     */
    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        /**
         * 获取用户地址信息
         */
        UmsMemberReceiveAddress umsMemberReceive = umsMemberService.getUmsMemberReceiveAddressById(memberId);
        modelMap.put("userAddressList", umsMemberReceive);
        modelMap.put("nickName", username);
        /**
         * 从购物车缓存获取选中的商品传给结算页面
         */
        List<OmsCartItem> omsCartItems = cartItemService.selectCartList(memberId);
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItems.add(omsOrderItem);
            }
        }
        modelMap.put("omsOrderItems", omsOrderItems);
        modelMap.put("totalAmount", getSumPrice(omsCartItems));
        /**
         * 生成一个唯一id，来检查订单的准确性
         */
        String tradeCode = orderService.genTradeCode(memberId);
        modelMap.put("tradeCode", tradeCode);
        return "trade";
    }

    /**
     * 重定向到支付系统
     *
     * @param deliveryAddress
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String deliveryAddress , String tradeCode, HttpServletRequest request, HttpServletResponse response) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        ModelAndView mv=new ModelAndView();
        /**
         * 检查两次tradeCode的一致性
         */
        String success = orderService.checkTradeCode(memberId, tradeCode);
        if (success.equals("success")) {
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            // 订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setDiscountAmount(null);
            //omsOrder.setFreightAmount(); 运费，支付后，在生成物流信息时
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(username);
            omsOrder.setNote("快点发货");
            String outTradeNo = "gmall";
            outTradeNo = outTradeNo + System.currentTimeMillis();// 将毫秒时间戳拼接到外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format(new Date());// 将时间字符串拼接到外部订单号

            omsOrder.setOrderSn(outTradeNo);//外部订单号
            omsOrder.setPayAmount(new BigDecimal(100));
            omsOrder.setOrderType(1);
            UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberService.getUmsMemberReceiveAddressById("1");
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            // 当前日期加一天，一天后配送
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            Date time = c.getTime();
            omsOrder.setReceiveTime(time);
            omsOrder.setSourceType(0);
            omsOrder.setStatus(0);
            omsOrder.setOrderType(0);
            omsOrder.setTotalAmount(new BigDecimal(1000));

            // 根据用户id获得要购买的商品列表(购物车)，和总价格
            List<OmsCartItem> omsCartItems = cartItemService.selectCartList(memberId);

            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    // 获得订单详情列表
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    // 检价
                   // boolean b = pmsSkuInfoService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                   //if (b == false) {
                    //    return "tradeFail";
                   // }
                    // 验库存,远程调用库存系统
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());

                    omsOrderItem.setOrderSn(outTradeNo);// 外部订单号，用来和其他系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("111111111111");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库对应的商品编号");// 在仓库中的skuId

                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItemList(omsOrderItems);

            // 将订单和订单详情写入数据库
            // 删除购物车的对应商品
            orderService.saveOrder(omsOrder);


            // 重定向到支付系统
            mv.setViewName("redirect:http://payment.gmall.com:8089/index");
            mv.addObject("outTradeNo",outTradeNo);
            BigDecimal amount=new BigDecimal("0.01");
            mv.addObject("totalAmount",amount);
            return  mv;
        } else {
            mv.setViewName("tradeFail");
            return mv;
        }
    }

    /**
     * 获取总金额
     *
     * @param omsCartItems
     * @return
     */
    private BigDecimal getSumPrice(List<OmsCartItem> omsCartItems) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();
            if (omsCartItem.getIsChecked().equals("1")) {
                bigDecimal = bigDecimal.add(totalPrice);
            }

        }
        return bigDecimal;
    }
}
