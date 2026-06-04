package com.scooter_backend.dto.nearest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record NearestScooterArgs(

        @JsonPropertyDescription("Foydalanuvchining joriy kenglik koordinatasi (latitude)")
        @JsonProperty(required = true) double latitude,

        @JsonPropertyDescription("Foydalanuvchining joriy uzunlik koordinatasi (longitude)")
        @JsonProperty(required = true) double longitude
) {
}
