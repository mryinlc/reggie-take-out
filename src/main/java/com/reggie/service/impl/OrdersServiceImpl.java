package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.dto.OrdersDto;
import com.reggie.mapper.OrdersMapper;
import com.reggie.pojo.*;
import com.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public Orders addOrder(Orders orders) {
        long userId = BaseContext.getUserId();
        // 根据Id获取地址信息
        AddressBook address = addressBookService.getById(orders.getAddressBookId());
        if (address == null)
            throw new CustomException("当前地址不存在");
        // 根据id获取用户信息
        User user = userService.getById(userId);
        if (user == null)
            throw new CustomException("当前用户不存在");
        // 根据userId获取购物车信息
        QueryWrapper<ShoppingCart> cartWrapper = new QueryWrapper<ShoppingCart>().eq("user_id", userId);
        List<ShoppingCart> cartList = shoppingCartService.list(cartWrapper);
        if (cartList == null)
            throw new CustomException("当前用户的购物车为空");
        // 设置orderDetail信息，并计算订单总金额
        BigDecimal totalAmount = new BigDecimal(0);
        long orderId = IdWorker.getId();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : cartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(cart.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(cart.getDishId());
            orderDetail.setDishFlavor(cart.getDishFlavor());
            orderDetail.setSetmealId(cart.getSetmealId());
            orderDetail.setNumber(cart.getNumber());
            orderDetail.setAmount(cart.getAmount());
            orderDetail.setImage(cart.getImage());
            totalAmount = totalAmount.add(cart.getAmount().multiply(new BigDecimal(cart.getNumber())));
            orderDetailList.add(orderDetail);
        }
        // 设置并保存orders信息
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(totalAmount);
        orders.setUserName(user.getName());
        orders.setPhone(address.getPhone());
        orders.setAddress(address.getDetail());
        orders.setConsignee(address.getConsignee());
        this.save(orders);
        // 保存orderDetail信息
        orderDetailService.saveBatch(orderDetailList);
        // 清空购物车
        shoppingCartService.remove(cartWrapper);
        return orders;
    }

    @Override
    public List<OrdersDto> getOrdersWithDetail() {
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        List<Orders> ordersList = this.list(new QueryWrapper<Orders>()
                .eq("user_id", BaseContext.getUserId())
                .orderByDesc("order_time"));
        for (Orders orders : ordersList) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            ordersDto.setOrderDetails(orderDetailService.list(new QueryWrapper<OrderDetail>()
                    .eq("order_id", ordersDto.getId())));
            ordersDtoList.add(ordersDto);
        }
        return ordersDtoList;
    }
}
