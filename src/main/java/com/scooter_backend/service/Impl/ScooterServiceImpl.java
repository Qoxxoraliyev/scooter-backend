package com.scooter_backend.service.Impl;

import com.scooter_backend.dto.scooter.ScooterCreateDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import com.scooter_backend.dto.scooter.ScooterStatusDTO;
import com.scooter_backend.dto.scooter.ScooterUpdateDTO;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.enums.ScooterStatus;
import com.scooter_backend.repository.ScooterRepository;
import com.scooter_backend.mapper.ScooterMapper;
import com.scooter_backend.service.ScooterService;
import com.scooter_backend.websocket.ScooterSocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ScooterServiceImpl implements ScooterService {

    private final ScooterRepository scooterRepository;

    private final ScooterSocketService scooterSocketService;

    public ScooterServiceImpl(ScooterRepository scooterRepository, ScooterSocketService scooterSocketService) {
        this.scooterRepository = scooterRepository;
        this.scooterSocketService = scooterSocketService;
    }


    @Override
    public ScooterResponseDTO create(ScooterCreateDTO dto) {

        Scooter scooter = new Scooter();

        scooter.setName(dto.name());
        scooter.setPricePerKm(dto.pricePerKm());

        scooter.setStatus(ScooterStatus.ACTIVE);
        scooter.setBatteryLevel(100);
        scooter.setLocked(true);
        scooter.setDeleted(false);

        scooterRepository.save(scooter);

        ScooterResponseDTO response = ScooterMapper.toDTO(scooter);
        scooterSocketService.sendScooterUpdate(response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScooterResponseDTO> getAll() {

        return scooterRepository.findAll()
                .stream()
                .filter(s -> !Boolean.TRUE.equals(s.getDeleted()))
                .map(ScooterMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ScooterResponseDTO getById(Long id) {

        Scooter scooter = getEntity(id);

        return ScooterMapper.toDTO(scooter);
    }


    @Override
    public ScooterResponseDTO updatePricePerKm(Long scooterId, BigDecimal pricePerKm) {
        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        if (pricePerKm == null || pricePerKm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price per km must be greater than 0");
        }

        scooter.setPricePerKm(pricePerKm);

        Scooter updatedScooter = scooterRepository.save(scooter);
        return ScooterMapper.toDTO(updatedScooter);
    }

    @Override
    public void updateStatus(Long id, ScooterStatusDTO dto) {

        Scooter scooter = getEntity(id);

        scooter.setStatus(dto.status());

        if (dto.status() == ScooterStatus.INACTIVE) {
            scooter.setLocked(true);
        }

        scooterRepository.save(scooter);
        scooterSocketService.sendStatusUpdate(ScooterMapper.toDTO(scooter));
    }

    @Override
    public ScooterResponseDTO update(Long id, ScooterUpdateDTO dto) {
        Scooter scooter = getEntity(id);

        scooter.setName(dto.name());
        scooter.setStatus(dto.status());
        scooter.setBatteryLevel(dto.batteryLevel());
        scooter.setLocked(dto.locked());
        scooter.setPricePerKm(dto.pricePerKm());

        if (dto.status() == ScooterStatus.INACTIVE) {
            scooter.setLocked(true);
        }

        Scooter savedScooter = scooterRepository.save(scooter);
        ScooterResponseDTO response = ScooterMapper.toDTO(savedScooter);

        scooterSocketService.sendScooterUpdate(response);
        scooterSocketService.sendStatusUpdate(response);

        return response;
    }


    @Override
    public void delete(Long id) {

        Scooter scooter = getEntity(id);

        scooter.setDeleted(true);
        scooterRepository.save(scooter);
        scooterSocketService.sendScooterUpdate(ScooterMapper.toDTO(scooter));
    }

    private Scooter getEntity(Long id) {

        return scooterRepository.findById(id)
                .filter(s -> !Boolean.TRUE.equals(s.getDeleted()))
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public long countAll() {
        return scooterRepository.countByDeletedFalse();
    }


    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return scooterRepository.countByStatusAndDeletedFalse(ScooterStatus.ACTIVE);
    }


}
