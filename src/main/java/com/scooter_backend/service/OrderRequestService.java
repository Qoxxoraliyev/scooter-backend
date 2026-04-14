package com.scooter_backend.service;

import com.scooter_backend.dto.order.OrderCreateDTO;
import com.scooter_backend.dto.order.OrderResponseDTO;

import java.util.List;

public interface OrderRequestService {
    OrderResponseDTO createOrder(String operatorPhone, OrderCreateDTO dto);
    List<OrderResponseDTO> getAvailableOrders();
    List<OrderResponseDTO> getMyAcceptedOrders(String driverPhone);
    List<OrderResponseDTO> getMyCreatedOrders(String operatorPhone);
    OrderResponseDTO acceptOrder(Long orderId, String driverPhone);
    OrderResponseDTO completeOrder(Long orderId, String driverPhone);
    OrderResponseDTO cancelOrder(Long orderId, String operatorPhone);
}