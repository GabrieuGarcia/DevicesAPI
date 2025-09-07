package com.devicesapi.infrastructure.web.controllers;

import com.devicesapi.application.dto.DeviceRequestDto;
import com.devicesapi.application.dto.DeviceResponseDto;
import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.exception.DeviceBadRequestException;
import com.devicesapi.domain.exception.DeviceNotFoundException;
import com.devicesapi.domain.ports.DeviceServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceServicePort deviceService;

    @PostMapping
    public ResponseEntity<Void> createDevice(@Valid @RequestBody DeviceRequestDto dto) {
        deviceService.createDevice(dto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDeviceById(@PathVariable UUID id) {
        return deviceService.getDeviceById(id)
                .map(device -> ResponseEntity.ok(DeviceResponseDto.fromDomain(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchDevice(@PathVariable UUID id, @RequestBody DeviceRequestDto dto) {
        deviceService.patchDevice(id, dto.toDomain());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{state}")
    public ResponseEntity<List<DeviceResponseDto>> getDeviceByState(@PathVariable String state) {
        State stateEnum;

        try {
            stateEnum = State.valueOf(state.toUpperCase());
        } catch (DeviceBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enum Value Not Found");
        }

        List<DeviceResponseDto> deviceResponseDtoList = deviceService.getDevicesByState(stateEnum)
                .stream()
                .map(DeviceResponseDto::fromDomain)
                .toList();

        return ResponseEntity.ok(deviceResponseDtoList);
    }

    @GetMapping("/{brand}")
    public ResponseEntity<List<DeviceResponseDto>> getDeviceByBrand(@PathVariable String brand) {
        Brand brandEnum = null;

        try {
            brandEnum = Brand.valueOf(brand.toUpperCase());
        } catch (DeviceBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enum Value Not Found");
        }

        List<DeviceResponseDto> deviceResponseDtoList = deviceService.getDevicesByBrand(brandEnum)
                .stream()
                .map(DeviceResponseDto::fromDomain)
                .toList();

        return ResponseEntity.ok(deviceResponseDtoList);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDto>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices().stream()
                .map(DeviceResponseDto::fromDomain)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(@PathVariable UUID id, @Valid @RequestBody DeviceRequestDto deviceRequestDto) {
        deviceService.updateDevice(id, deviceRequestDto.toDomain());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }
}