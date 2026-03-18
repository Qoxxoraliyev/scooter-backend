package com.scooter_backend.dto.websocket;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeeklyStatsDTO(

        LocalDate date,

        // 🚕 rides
        long totalRides,
        long completedRides,
        long cancelledRides,

        // 💰 money
        BigDecimal revenue

) {}