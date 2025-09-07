package com.devicesapi.application.dto;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceResponseDto(
        UUID id,
        String name,
        Brand brand,
        State state,
        LocalDateTime creationTime
) {
    public static DeviceResponseDto fromDomain(Device device) {
        return new DeviceResponseDto(
                device.getId(),
                device.getName(),
                device.getBrand(),
                device.getState(),
                device.getCreationTime()
        );
    }
}