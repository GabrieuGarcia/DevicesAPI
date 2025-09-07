package com.devicesapi.domain.services;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.exception.DeviceNotFoundException;
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

    private final DevicePersistencePort devicePersistencePort;

    public void createDevice(Device device) {
        devicePersistencePort.save(device);
    }

    public Optional<Device> getDeviceById(UUID id) {
        return devicePersistencePort.findById(id);
    }

    public List<Device> getAllDevices() {
        return devicePersistencePort.findAll();
    }

    public List<Device> getDevicesByBrand(Brand brand) {
        return devicePersistencePort.findByBrand(brand);
    }

    public List<Device> getDevicesByState(State state) {
        return devicePersistencePort.findByState(state);
    }

    public void patchDevice(UUID id, Device deviceToBePatched) {
        Device existingDevice = getDeviceById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device with id '" + id + "' not found"));

        validateDeviceToBeUpdated(deviceToBePatched, existingDevice);

        String name = deviceToBePatched.getName().isBlank() ? existingDevice.getName() : deviceToBePatched.getName();
        Brand brand = deviceToBePatched.getBrand() == null ? existingDevice.getBrand() : deviceToBePatched.getBrand();
        State state = deviceToBePatched.getState() == null ? existingDevice.getState() : deviceToBePatched.getState();

        Device deviceToBeSaved = Device.updateDevice(id, name, brand, state,
                //Cannot be updated
                existingDevice.getCreationTime());

        devicePersistencePort.save(deviceToBeSaved);
    }

    public void updateDevice(UUID id, Device updatedDevice) {
        Device existingDevice = getDeviceById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device with id '" + id + "' not found"));
        validateDeviceToBeUpdated(updatedDevice, existingDevice);

        Device deviceToBeSaved = Device.updateDevice(id,
                updatedDevice.getName(),
                updatedDevice.getBrand(),
                updatedDevice.getState(),
                //Cannot be updated
                existingDevice.getCreationTime());

        devicePersistencePort.save(deviceToBeSaved);
    }

    public void deleteDevice(UUID id) {
        Device existingDevice = devicePersistencePort.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device with id '" + id + "' not found"));

        if (isInUse(existingDevice)) {
            throw new DeviceNotFoundException("Device with id '" + id + "' is still in use and cannot be deleted");
        }
        devicePersistencePort.deleteById(id);
    }

    private void validateDeviceToBeUpdated(Device deviceToBeUpdated, Device existingDevice) {
        if (isInUse(deviceToBeUpdated) && isNameOrBrandDifferent(deviceToBeUpdated, existingDevice)) {
            throw new DeviceNotFoundException("Device with id '" + deviceToBeUpdated.getId() + "' is still in use so name and and cannot be updated");
        }
    }

    private boolean isNameOrBrandDifferent(Device deviceToBeUpdated, Device existingDevice) {
        return !deviceToBeUpdated.getName().equals(existingDevice.getName()) || !deviceToBeUpdated.getBrand().equals(existingDevice.getBrand());
    }

    private boolean isInUse(Device device) {
        return device.getState() == State.IN_USE;
    }
}