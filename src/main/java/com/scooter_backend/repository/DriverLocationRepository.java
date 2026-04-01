package com.scooter_backend.repository;

import com.scooter_backend.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation,Long> {

    Optional<DriverLocation> findTopByDriverIdOrderByCreatedAtDesc(Long driverId);

}
