package com.devicesapi.application.dto;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRequestDto(
                String name,
                Brand brand,
                State state
) {
    public Device toDomain() {
        return Device.createNew(this.name, this.brand, this.state);
    }
}