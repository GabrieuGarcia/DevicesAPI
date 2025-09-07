package com.devicesapi.domain.ports;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceServicePort {

    void createDevice(Device device);

    Optional<Device> getDeviceById(UUID id);

    void patchDevice(UUID id, Device device);

    List<Device> getAllDevices();

    List<Device> getDevicesByBrand(Brand brand);

    List<Device> getDevicesByState(State state);

    void updateDevice(UUID id, Device updatedDevice);

    void deleteDevice(UUID id);
}