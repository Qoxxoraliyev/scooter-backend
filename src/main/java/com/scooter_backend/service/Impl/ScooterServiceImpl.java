package com.scooter_backend.service.Impl;

import com.scooter_backend.dto.scooter.ScooterCreateDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import com.scooter_backend.dto.scooter.ScooterStatusDTO;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.enums.ScooterStatus;
import com.scooter_backend.repository.ScooterRepository;
import com.scooter_backend.mapper.ScooterMapper;
import com.scooter_backend.service.ScooterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScooterServiceImpl implements ScooterService {

    private final ScooterRepository scooterRepository;

    public ScooterServiceImpl(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public ScooterResponseDTO create(ScooterCreateDTO dto) {

        Scooter scooter = new Scooter();

        scooter.setName(dto.name());
        scooter.setPricePerMinute(dto.pricePerMinute());

        scooter.setStatus(ScooterStatus.ACTIVE);
        scooter.setBatteryLevel(100);
        scooter.setLocked(true);
        scooter.setDeleted(false);

        scooterRepository.save(scooter);

        return ScooterMapper.toDTO(scooter);
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
    public void updateStatus(Long id, ScooterStatusDTO dto) {

        Scooter scooter = getEntity(id);

        scooter.setStatus(dto.status());

        if (dto.status() == ScooterStatus.INACTIVE) {
            scooter.setLocked(true);
        }
    }

    @Override
    public void delete(Long id) {

        Scooter scooter = getEntity(id);

        scooter.setDeleted(true);
    }

    private Scooter getEntity(Long id) {

        return scooterRepository.findById(id)
                .filter(s -> !Boolean.TRUE.equals(s.getDeleted()))
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
    }



}
