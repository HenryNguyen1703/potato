package com.ktpm.potatoapi.drone.service;

import com.ktpm.potatoapi.drone.dto.DroneRequest;
import com.ktpm.potatoapi.drone.dto.DroneResponse;
import jakarta.mail.MessagingException;

public interface DroneService {
    DroneResponse getDrone(Long id);
    DroneResponse createDrone(DroneRequest request);
    DroneResponse updateDroneLocation(Long id, double latitude, double longitude) throws MessagingException;
    DroneResponse updateDroneStatus(Long id, String status);
    void deleteDrone(Long id);
}