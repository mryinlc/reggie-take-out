package com.reggie.dto;

import com.reggie.pojo.OrderDetail;
import com.reggie.pojo.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
	
}
