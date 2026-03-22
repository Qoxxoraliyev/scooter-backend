package com.scooter_backend.repository;

import com.scooter_backend.entity.Ride;
import com.scooter_backend.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {


    List<Ride> findByUserId(Long userId);

    List<Ride> findByDriver_Id(Long driverId);

    @Modifying
    @Query("DELETE FROM Ride r WHERE r.createdAt < :time")
    void deleteOldRides(LocalDateTime time);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByScooter_IdAndCreatedAtBetween(Long scooterId, LocalDateTime start, LocalDateTime end);

    boolean existsByScooterIdAndStatus(Long scooterId, RideStatus status);
}
