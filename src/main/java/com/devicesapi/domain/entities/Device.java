package com.devicesapi.domain.entities;

import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class Device {

    UUID id;

    @NotBlank(message = "Device name is required")
    String name;

    @NotNull(message = "Device brand is required")
    Brand brand;

    @NotNull(message = "Device state is required")
    State state;

    LocalDateTime creationTime;

    // Static factory methods for creating devices
    public static Device createNew(String name, Brand brand, State state) {
        return new Device(null, name, brand, state, LocalDateTime.now());
    }

    public static Device updateDevice(UUID id, String name, Brand brand, State state, LocalDateTime creationTime) {
        return new Device(id, name, brand, state, creationTime);
    }
}