package com.scooter_backend.service.nearest;

import com.scooter_backend.dto.nearest.NearestScooterArgs;
import com.scooter_backend.dto.nearest.NearestScooterResponse;
import com.scooter_backend.enums.ScooterStatus;
import com.scooter_backend.repository.ScooterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service("nearestScooterFunction")
public class NearestScooterService implements Function<NearestScooterArgs, NearestScooterResponse> {

    private final ScooterRepository scooterRepository;

    public NearestScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public NearestScooterResponse apply(NearestScooterArgs args) {
        // Hozirda ijaraga olishga tayyor (AVAILABLE) skuterlarni qidiramiz
        List<Object[]> results = scooterRepository.findNearestScootersWithDistance(
                args.latitude(),
                args.longitude(),
                ScooterStatus.ACTIVE
        );

        // Agar birorta ham skuter topilmasa null qaytaramiz (AI buni o'zi tushunib oladi)
        if (results == null || results.isEmpty()) {
            return new NearestScooterResponse(
                    null, "Yaqin atrofda skuterlar topilmadi",
                    0.0, 0.0, 0.0, 0, "NOT_AVAILABLE"
            );
        }

        // Ro'yxat masofa bo'yicha ORDER BY qilingan, shuning uchun 0-indeksdagi eng yaqini bo'ladi
        Object[] nearestRow = results.get(0);

        // Native query qaytargan SQL ustunlarining indeks bo'yicha mosligi:
        // Diqqat: SELECT s.* orqali keladigan ustunlar tartibi jadvaldagi tartib bilan bir xil bo'ladi.
        // Agar xatolik bo'lmasligi uchun aliaslardan o'qish xavfsizroq, lekin sodda holatda quyidagicha parse qilamiz:

        Long id = ((Number) nearestRow[0]).longValue();               // s.id
        Integer batteryLevel = ((Number) nearestRow[1]).intValue();   // s.battery_level
        // (Sizning jadval tuzilmangizda ustunlar tartibiga qarab indekslarni tekshiring, yoki alias bering)
        String name = (String) nearestRow[6];                         // s.name
        String status = (String) nearestRow[7];                       // s.status

        // Alias qilingan ustunlar odatda asosiy jadval ustunlaridan keyin keladi:
        double lat = ((Number) nearestRow[nearestRow.length - 3]).doubleValue(); // loc_lat
        double lon = ((Number) nearestRow[nearestRow.length - 2]).doubleValue(); // loc_lon
        double distanceMeters = ((Number) nearestRow[nearestRow.length - 1]).doubleValue(); // distance

        return new NearestScooterResponse(
                id,
                name,
                lat,
                lon,
                Math.round(distanceMeters * 10.0) / 10.0, // Masofani 1 xonagacha yaxlitlash (masalan: 145.5 metr)
                batteryLevel,
                status
        );
    }
}