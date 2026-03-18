package com.scooter_backend.service.yandex;

import com.scooter_backend.dto.yandex.YandexDistanceResponse;
import org.springframework.beans.factory.annotation.Value;import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class YandexRoutingService {

    @Value("${yandex.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public YandexRoutingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public YandexDistanceResponse getDistanceAndTime(
            double startLat,
            double startLon,
            double endLat,
            double endLon
    ) {

        String url = String.format(
                "https://api.routing.yandex.net/v2/distancematrix" +
                        "?origins=%f,%f&destinations=%f,%f&apikey=%s",
                startLat, startLon,
                endLat, endLon,
                apiKey
        );

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        Map body = response.getBody();

        Map rows = (Map) ((List) body.get("rows")).get(0);
        Map elements = (Map) ((List) rows.get("elements")).get(0);

        double distance = ((Number) elements.get("distance")).doubleValue();
        double duration = ((Number) elements.get("duration")).doubleValue();

        return new YandexDistanceResponse(distance, duration);
    }
}

