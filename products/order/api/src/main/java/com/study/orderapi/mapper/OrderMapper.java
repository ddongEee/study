package com.study.orderapi.mapper;

import com.study.orderapi.model.Order;
import com.study.orderapi.rest.dto.CreateOrderRequest;
import com.study.orderapi.rest.dto.OrderDto;

public interface OrderMapper {

    Order toOrder(CreateOrderRequest createOrderRequest);

    OrderDto toOrderDto(Order order);
}
