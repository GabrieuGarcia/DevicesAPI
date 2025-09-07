package com.devicesapi.infrastructure.adapters;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.ports.DevicePersistencePort;
import com.devicesapi.infrastructure.persistence.entities.DeviceEntity;
import com.devicesapi.infrastructure.persistence.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DevicePersistence implements DevicePersistencePort {

    private final DeviceRepository deviceRepository;

    @Override
    public Device save(Device device) {
        DeviceEntity savedEntity = deviceRepository.save(DeviceEntity.fromDomain(device));
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Device> findById(UUID id) {
        return deviceRepository.findById(id)
                .map(DeviceEntity::toDomain);
    }

    @Override
    public List<Device> findAll() {
        return deviceRepository.findAll()
                .stream()
                .map(DeviceEntity::toDomain)
                .toList();
    }

    @Override
    public List<Device> findByBrand(Brand brand) {
        return deviceRepository.findByBrand(brand)
                .stream()
                .map(DeviceEntity::toDomain)
                .toList();
    }

    @Override
    public List<Device> findByState(State state) {
        return deviceRepository.findByState(state)
                .stream()
                .map(DeviceEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return deviceRepository.existsById(id);
    }
}