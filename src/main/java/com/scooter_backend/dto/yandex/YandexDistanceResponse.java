package com.scooter_backend.dto.yandex;

public record YandexDistanceResponse(
        double distanceMeters,
        double durationSeconds
) {}
