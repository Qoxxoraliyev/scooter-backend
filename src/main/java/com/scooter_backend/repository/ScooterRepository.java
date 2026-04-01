package com.scooter_backend.repository;

import com.scooter_backend.entity.Scooter;
import com.scooter_backend.enums.RideStatus;
import com.scooter_backend.enums.ScooterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter,Long> {

    long countByDeletedFalse();

    long countByStatusAndDeletedFalse(ScooterStatus status);

    Optional<Scooter> findFirstByDriverIsNullAndStatusAndIsLockedFalseAndDeletedFalse(ScooterStatus status);


}
