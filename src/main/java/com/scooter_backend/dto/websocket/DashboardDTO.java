package com.scooter_backend.dto.websocket;

import java.math.BigDecimal;

public record DashboardDTO(

        // 👥 USERS
        long totalUsers,
        long totalDrivers,

        // 🛴 SCOOTERS
        long totalScooters,
        long availableScooters,
        long inUseScooters,
        long offlineScooters,

        // 🚕 RIDES
        long totalRides,
        long activeRides,
        long completedRides,
        long cancelledRides,

        // 💰 MONEY
        BigDecimal totalRevenue,
        BigDecimal todayRevenue,

        // ⚡ REALTIME
        long onlineDrivers

) {}