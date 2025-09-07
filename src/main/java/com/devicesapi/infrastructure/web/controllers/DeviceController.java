package com.devicesapi.infrastructure.web.controllers;

import com.devicesapi.application.dto.DeviceRequestDto;
import com.devicesapi.application.dto.DeviceResponseDto;
import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.ports.DeviceServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceServicePort deviceService;

    @PostMapping
    public ResponseEntity<DeviceResponseDto> createDevice(@Valid @RequestBody DeviceRequestDto deviceRequestDto) {
        Device createdDevice = deviceService.createDevice(deviceRequestDto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(DeviceResponseDto.fromDomain(createdDevice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDeviceById(@PathVariable UUID id) {
        return deviceService.getDeviceById(id)
                .map(device -> ResponseEntity.ok(DeviceResponseDto.fromDomain(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDto>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices().stream()
                .map(DeviceResponseDto::fromDomain)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(@PathVariable UUID id, @Valid @RequestBody DeviceRequestDto deviceRequestDto) {
        Device savedDevice = deviceService.updateDevice(id, deviceRequestDto.toDomain());
        return ResponseEntity.ok(DeviceResponseDto.fromDomain(savedDevice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }
}