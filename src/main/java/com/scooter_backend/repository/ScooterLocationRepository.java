package com.scooter_backend.repository;

import com.scooter_backend.entity.ScooterLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.Optional;

@Repository
public interface ScooterLocationRepository extends JpaRepository<ScooterLocation,Long> {

    @Modifying
    @Query("DELETE FROM ScooterLocation s WHERE s.updatedAt < :time")
    void deleteOldLocations(LocalDateTime time);

    Optional<ScooterLocation> findByScooter_Id(Long scooterId);
}
