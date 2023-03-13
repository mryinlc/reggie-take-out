package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.OrdersDto;
import com.reggie.pojo.Orders;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    Orders addOrder(Orders orders);

    List<OrdersDto> getOrdersWithDetail();
}
