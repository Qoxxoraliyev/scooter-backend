package com.scooter_backend.repository;

import com.scooter_backend.entity.Scooter;
import com.scooter_backend.enums.ScooterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {

    long countByDeletedFalse();

    long countByStatusAndDeletedFalse(ScooterStatus status);

    Optional<Scooter> findFirstByDriverIsNullAndStatusAndIsLockedTrueAndDeletedFalse(ScooterStatus status);

    /**
     * Foydalanuvchi koordinatasiga ko'ra eng yaqin, o'chirilmagan va berilgan statusdagi skuterlar ro'yxatini qaytaradi.
     * Masofa metrda hisoblanadi (Yer radiusi: 6371000 metr).
     */
    @Query(value = """
        SELECT s.*, sl.latitude as loc_lat, sl.longitude as loc_lon,
        (6371000 * acos(cos(radians(:userLat)) * cos(radians(sl.latitude)) * cos(radians(sl.longitude) - radians(:userLon)) + sin(radians(:userLat)) * sin(radians(sl.latitude)))) AS distance 
        FROM scooters s
        JOIN scooter_locations sl ON s.id = sl.scooter_id
        WHERE s.deleted = false AND s.status = :#{#status.name()}
        ORDER BY distance ASC
        """, nativeQuery = true)
    List<Object[]> findNearestScootersWithDistance(
            @Param("userLat") double userLat,
            @Param("userLon") double userLon,
            @Param("status") ScooterStatus status
    );
}