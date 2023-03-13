package com.reggie.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.config.AlipayConfig;
import com.reggie.dto.OrdersDto;
import com.reggie.pojo.OrderDetail;
import com.reggie.pojo.Orders;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/order")
@RestController
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<Orders> addOrder(@RequestBody Orders orders) {
        return R.success(ordersService.addOrder(orders));
    }

    @GetMapping("/userPage")
    public R<Page> getOrders(int page, int pageSize) {
        Page<Orders> ordersPage = ordersService.page(new Page<>(page, pageSize), new QueryWrapper<Orders>()
                .eq("user_id", BaseContext.getUserId())
                .orderByDesc("order_time"));
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for (Orders orders : ordersPage.getRecords()) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            ordersDto.setOrderDetails(orderDetailService.list(new QueryWrapper<OrderDetail>()
                    .eq("order_id", ordersDto.getId())));
            ordersDtoList.add(ordersDto);
        }
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

    @GetMapping("/page")
    public R<Page> adminGetOrders(int page, int pageSize, Integer number, String beginTime, String endTime) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number != null, Orders::getNumber, number)
                .ge(beginTime != null, Orders::getOrderTime, beginTime)
                .le(endTime != null, Orders::getOrderTime, endTime);
        return R.success(ordersService.page(new Page<>(page, pageSize), queryWrapper));
    }

    @PutMapping
    public R<Object> updateOrder(@RequestBody Orders orders) {
        return ordersService.updateById(orders) ? R.success(null) : R.error("修改订单状态失败");
    }
}
