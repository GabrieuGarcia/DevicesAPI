package com.devicesapi.domain.services;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.ports.DevicePersistencePort;
import com.devicesapi.domain.ports.DeviceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService implements DeviceServicePort {

    private final DevicePersistencePort devicePort;

    public Device createDevice(Device device) {
        return devicePort.save(device);
    }

    public Optional<Device> getDeviceById(UUID id) {
        return devicePort.findById(id);
    }

    public List<Device> getAllDevices() {
        return devicePort.findAll();
    }

    public List<Device> getDevicesByBrand(Brand brand) {
        return devicePort.findByBrand(brand);
    }

    public List<Device> getDevicesByState(State state) {
        return devicePort.findByState(state);
    }

    public Device updateDevice(UUID id, Device updatedDevice) {
        Device existingDevice = devicePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device with id '" + id + "' not found"));

        Device deviceToBeSaved = Device.updateDevice(id,
                updatedDevice.getName(),
                updatedDevice.getBrand(),
                updatedDevice.getState(),
                //Cannot be updated
                existingDevice.getCreationTime());

        return devicePort.save(deviceToBeSaved);
    }

    public boolean deleteDevice(UUID id) {
        Device deviceToBeDeleted = devicePort.findById(id).orElseThrow(() -> new IllegalArgumentException("Device with id '" + id + "' not found"));
        if(!State.IN_USE.equals(deviceToBeDeleted.getState())){
            devicePort.deleteById(id);
            return true;
        }
        return false;
    }
}