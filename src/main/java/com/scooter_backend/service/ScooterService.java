package com.scooter_backend.service;

import com.scooter_backend.dto.scooter.ScooterCreateDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import com.scooter_backend.dto.scooter.ScooterStatusDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ScooterService {

    ScooterResponseDTO create(ScooterCreateDTO dto);

    List<ScooterResponseDTO> getAll();

    ScooterResponseDTO getById(Long id);

    void updateStatus(Long id, ScooterStatusDTO dto);

    void delete(Long id);

    long countAll();

    long countActive();

    ScooterResponseDTO updatePricePerKm(Long scooterId, BigDecimal pricePerKm);
}
