package com.scooter_backend.controller;

import com.scooter_backend.dto.user.*;
import com.scooter_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserCreateDTO dto
    ) {
        return ResponseEntity.ok(userService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll(
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.ok(userService.getAll(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserPasswordUpdateDTO dto
    ) {
        userService.updatePassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


}