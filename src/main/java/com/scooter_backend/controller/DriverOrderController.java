package com.scooter_backend.controller;


import com.scooter_backend.dto.order.OrderResponseDTO;
import com.scooter_backend.service.OrderRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver/orders")
public class DriverOrderController {

    private final OrderRequestService orderRequestService;

    public DriverOrderController(OrderRequestService orderRequestService) {
        this.orderRequestService = orderRequestService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<OrderResponseDTO>> getAvailableOrders() {
        return ResponseEntity.ok(orderRequestService.getAvailableOrders());
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderRequestService.getMyAcceptedOrders(authentication.getName()));
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<OrderResponseDTO> acceptOrder(@PathVariable Long orderId,
                                                        Authentication authentication) {
        return ResponseEntity.ok(orderRequestService.acceptOrder(orderId, authentication.getName()));
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponseDTO> completeOrder(@PathVariable Long orderId,
                                                          Authentication authentication) {
        return ResponseEntity.ok(orderRequestService.completeOrder(orderId, authentication.getName()));
    }
}
