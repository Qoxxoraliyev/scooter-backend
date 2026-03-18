package com.scooter_backend.dto.location;

import jakarta.validation.constraints.*;

public record LocationDTO(

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude

) {}