package com.reggie.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.CustomException;
import com.reggie.config.AlipayConfig;
import com.reggie.pojo.Orders;
import com.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrdersService ordersService;


    @ResponseBody
    @GetMapping("/page")
    public String getPayPage(Long orderId, HttpSession session) {
        Orders order = ordersService.getById(orderId);
        session.setAttribute("orderId", orderId);
        try {
            return sendRequestToAlipay(order.getId().toString(), order.getAmount().doubleValue(), order.getConsignee() + "的订单");
        } catch (AlipayApiException e) {
            throw new CustomException("跳转支付过程出错");
        }
    }

    /**
     * 请求支付宝支付页面
     *
     * @param outTradeNo  订单编号
     * @param totalAmount 金额
     * @param subject     订单名称
     * @return
     * @throws AlipayApiException
     */
    private String sendRequestToAlipay(String outTradeNo, Double totalAmount, String subject) throws AlipayApiException {
        // 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.GATEWAY_URL, AlipayConfig.APP_ID,
                AlipayConfig.APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGN_TYPE);

        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.RETURN_URL);
        // alipayRequest.setNotifyUrl(AlipayConfig.NOTIFY_URL);

        // 商品描述（可空）
        String body = "";
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        // 请求
        return alipayClient.pageExecute(alipayRequest).getBody();
    }

    @RequestMapping("/return")
    public String returnUrlMethod(HttpServletRequest request, HttpSession session, Model model) throws AlipayApiException, UnsupportedEncodingException {
        System.out.println("=================================同步回调=====================================");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        // System.out.println(params);// 查看参数都有哪些
        log.info(params.toString());
        // 验证签名（支付宝公钥）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE); // 调用SDK验证签名
        // 验证签名通过
        if (signVerified) {
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易流水号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            Double money = Double.parseDouble(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

            log.info("商户订单号=" + out_trade_no);
            log.info("支付宝交易号=" + trade_no);
            log.info("付款金额=" + money);

            // 在这里编写自己的业务代码（对数据库的操作）
			/*
			################################
			*/
            // 跳转到提示页面（成功或者失败的提示页面）
            Long orderId = Long.parseLong(session.getAttribute("orderId").toString());
            Orders orders = new Orders();
            orders.setId(orderId);
            orders.setStatus(2);
            ordersService.updateById(orders);
            return "redirect:/front/page/pay-success.html";
        }
        return "redirect:/front/page/pay-fail.html";
    }
}
