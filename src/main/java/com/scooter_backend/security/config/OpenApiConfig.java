package com.scooter_backend.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Scooter Backend API",
                version = "1.0.0",
                description = "REST API for Scooter Sharing System 🚀\n\n" +
                        "Features:\n" +
                        "- JWT Authentication\n" +
                        "- Scooter management\n" +
                        "- Ride lifecycle\n" +
                        "- Real-time location (Redis + WebSocket)\n" +
                        "- Admin dashboard\n\n" +
                        "Authorization:\n" +
                        "👉 Use Bearer token: `Bearer <your_token>`",
                contact = @Contact(
                        name = "Muhammadali",
                        email = "javabackenddeveloperbro@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://springdoc.org"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authorization header using Bearer scheme. Example: 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}