package com.scooter_backend.dto.order;

import com.scooter_backend.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponseDTO(
        Long id,
        String from,
        String to,
        String clientPhone,
        OrderStatus status,
        Long createdByOperatorId,
        Long acceptedByDriverId,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt,
        LocalDateTime completedAt

) {}
