package com.devicesapi.domain.ports;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceServicePort {
    
    Device createDevice(Device device);
    
    Optional<Device> getDeviceById(UUID id);
    
    List<Device> getAllDevices();
    
    List<Device> getDevicesByBrand(Brand brand);
    
    List<Device> getDevicesByState(State state);
    
    Device updateDevice(UUID id, Device updatedDevice);
    
    boolean deleteDevice(UUID id);
}