package com.scooter_backend.controller;
import com.scooter_backend.dto.scooter.ScooterCreateDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import com.scooter_backend.dto.scooter.ScooterStatusDTO;
import com.scooter_backend.service.ScooterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/scooters")
public class ScooterController {

    private final ScooterService scooterService;

    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @PostMapping
    public ResponseEntity<ScooterResponseDTO> create(
            @RequestBody ScooterCreateDTO dto
    ) {
        ScooterResponseDTO response = scooterService.create(dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<ScooterResponseDTO>> getAll() {
        return ResponseEntity.ok(scooterService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ScooterResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(scooterService.getById(id));
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody ScooterStatusDTO dto
    ) {
        scooterService.updateStatus(id, dto);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        scooterService.delete(id);
        return ResponseEntity.noContent().build();
    }


}