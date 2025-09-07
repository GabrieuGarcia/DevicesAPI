package com.devicesapi.domain.ports;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DevicePersistencePort {
    Device save(Device device);

    Optional<Device> findById(UUID id);

    List<Device> findAll();

    List<Device> findByBrand(Brand brand);

    List<Device> findByState(State state);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}