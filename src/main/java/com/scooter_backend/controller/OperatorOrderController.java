package com.scooter_backend.controller;

import com.scooter_backend.dto.order.OrderCreateDTO;
import com.scooter_backend.dto.order.OrderResponseDTO;
import com.scooter_backend.service.OrderRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operator/orders")
public class OperatorOrderController {

    private final OrderRequestService orderRequestService;

    public OperatorOrderController(OrderRequestService orderRequestService) {
        this.orderRequestService = orderRequestService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO dto,
                                                        Authentication authentication) {
        return ResponseEntity.ok(orderRequestService.createOrder(authentication.getName(), dto));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderRequestService.getMyCreatedOrders(authentication.getName()));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId,
                                                        Authentication authentication) {
        return ResponseEntity.ok(orderRequestService.cancelOrder(orderId, authentication.getName()));
    }


}
