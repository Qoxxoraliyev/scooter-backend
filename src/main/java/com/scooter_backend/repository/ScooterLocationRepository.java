package com.scooter_backend.repository;

import com.scooter_backend.entity.ScooterLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ScooterLocationRepository extends JpaRepository<ScooterLocation,Long> {

}
