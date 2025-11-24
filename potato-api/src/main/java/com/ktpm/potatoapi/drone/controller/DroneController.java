package com.ktpm.potatoapi.drone.controller;

import com.ktpm.potatoapi.drone.dto.DroneRequest;
import com.ktpm.potatoapi.drone.service.DroneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/drones")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Drone APIs", description = "APIs for drone")
public class DroneController {
    DroneService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDrone(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDrone(id));
    }

    @PostMapping
    public ResponseEntity<?> createDroneInStation(@RequestBody @Valid DroneRequest request) {
        return ResponseEntity.ok(service.createDrone(request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateDroneLocation(@PathVariable Long id,
                                                 @RequestParam double latitude,
                                                 @RequestParam double longitude) {
        return ResponseEntity.ok(service.updateDroneLocation(id, latitude, longitude));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDrone(@PathVariable Long id) {
        service.deleteDrone(id);
        return ResponseEntity.ok().build();
    }
}