package com.scooter_backend.repository;

import com.scooter_backend.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation,Long> {

    Optional<DriverLocation> findTopByDriverIdOrderByCreatedAtDesc(Long driverId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DriverLocation d WHERE d.createdAt < :threshold")
    void deleteOldDriverLocations(LocalDateTime threshold);

}
