package com.devicesapi.domain.services;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.exception.DeviceNotFoundException;
import com.devicesapi.domain.exception.DeviceBusinessException;
import com.devicesapi.domain.ports.DevicePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DevicePersistencePort devicePersistencePort;

    @InjectMocks
    private DeviceService deviceService;

    private Device testDevice;
    private UUID testId;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testTime = LocalDateTime.now();
        testDevice = Device.createWithIdAndTime(testId, "Test Device", Brand.SAMSUNG, State.AVAILABLE, testTime);
    }

    @Test
    void deleteDevice_WhenDeviceInUse_ShouldThrowException() {
        // Given
        Device inUseDevice = Device.createWithIdAndTime(testId, "In Use Device", Brand.SAMSUNG, State.IN_USE, testTime);
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.deleteDevice(testId))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("is still in use and cannot be deleted");
        
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).deleteById(any());
    }

    @Test
    void createDevice_ShouldReturnSavedDevice() {
        // Given
        Device newDevice = Device.createNew("New Device", Brand.APPLE, State.AVAILABLE);
        when(devicePersistencePort.save(newDevice)).thenReturn(testDevice);

        // When
        Device result = deviceService.createDevice(newDevice);

        // Then
        assertThat(result).isEqualTo(testDevice);
        verify(devicePersistencePort).save(newDevice);
    }

    @Test
    void getDeviceById_WhenDeviceExists_ShouldReturnDevice() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));

        // When
        Optional<Device> result = deviceService.getDeviceById(testId);

        // Then
        assertThat(result).isPresent().contains(testDevice);
        verify(devicePersistencePort).findById(testId);
    }

    @Test
    void getDeviceById_WhenDeviceNotExists_ShouldReturnEmpty() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When
        Optional<Device> result = deviceService.getDeviceById(testId);

        // Then
        assertThat(result).isEmpty();
        verify(devicePersistencePort).findById(testId);
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() {
        // Given
        Device device2 = Device.createWithIdAndTime(UUID.randomUUID(), "Device 2", Brand.GOOGLE, State.IN_USE, testTime);
        List<Device> devices = Arrays.asList(testDevice, device2);
        when(devicePersistencePort.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceService.getAllDevices();

        // Then
        assertThat(result).hasSize(2).containsExactly(testDevice, device2);
        verify(devicePersistencePort).findAll();
    }

    @Test
    void getDevicesByBrand_ShouldReturnDevicesWithSpecificBrand() {
        // Given
        List<Device> samsungDevices = Arrays.asList(testDevice);
        when(devicePersistencePort.findByBrand(Brand.SAMSUNG)).thenReturn(samsungDevices);

        // When
        List<Device> result = deviceService.getDevicesByBrand(Brand.SAMSUNG);

        // Then
        assertThat(result).hasSize(1).containsExactly(testDevice);
        verify(devicePersistencePort).findByBrand(Brand.SAMSUNG);
    }

    @Test
    void getDevicesByState_ShouldReturnDevicesWithSpecificState() {
        // Given
        List<Device> availableDevices = Arrays.asList(testDevice);
        when(devicePersistencePort.findByState(State.AVAILABLE)).thenReturn(availableDevices);

        // When
        List<Device> result = deviceService.getDevicesByState(State.AVAILABLE);

        // Then
        assertThat(result).hasSize(1).containsExactly(testDevice);
        verify(devicePersistencePort).findByState(State.AVAILABLE);
    }

    @Test
    void updateDevice_WhenDeviceExistsAndNotInUse_ShouldUpdateDevice() {
        // Given
        Device updatedDevice = Device.createNew("Updated Device", Brand.APPLE, State.AVAILABLE);
        Device expectedDevice = Device.createWithIdAndTime(testId, "Updated Device", Brand.APPLE, State.AVAILABLE, testTime);
        
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));
        when(devicePersistencePort.save(any(Device.class))).thenReturn(expectedDevice);

        // When
        Device result = deviceService.updateDevice(testId, updatedDevice);

        // Then
        assertThat(result).isEqualTo(expectedDevice);
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort).save(any(Device.class));
    }

    @Test
    void updateDevice_WhenDeviceNotExists_ShouldThrowException() {
        // Given
        Device updatedDevice = Device.createNew("Updated Device", Brand.APPLE, State.AVAILABLE);
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deviceService.updateDevice(testId, updatedDevice))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Device with id '" + testId + "' not found");
        
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).save(any());
    }

    @Test
    void updateDevice_WhenDeviceInUse_ShouldThrowException() {
        // Given
        Device inUseDevice = Device.createWithIdAndTime(testId, "In Use Device", Brand.SAMSUNG, State.IN_USE, testTime);
        Device updatedDevice = Device.createNew("Updated Device", Brand.APPLE, State.AVAILABLE);
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.updateDevice(testId, updatedDevice))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("is still in use so name and and cannot be updated");
        
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).save(any());
    }

    @Test
    void patchDevice_WhenDeviceExistsAndNotInUse_ShouldPatchDevice() {
        // Given
        Device patchDevice = Device.createNew("Patched Device", Brand.GOOGLE, State.INACTIVE);
        Device expectedDevice = Device.createWithIdAndTime(testId, "Patched Device", Brand.GOOGLE, State.INACTIVE, testTime);
        
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));
        when(devicePersistencePort.save(any(Device.class))).thenReturn(expectedDevice);

        // When
        deviceService.patchDevice(testId, patchDevice);

        // Then
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort).save(any(Device.class));
    }

    @Test
    void patchDevice_WhenDeviceNotExists_ShouldThrowException() {
        // Given
        Device patchDevice = Device.createNew("Patched Device", Brand.GOOGLE, State.INACTIVE);
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deviceService.patchDevice(testId, patchDevice))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Device with id '" + testId + "' not found");
        
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).save(any());
    }

    @Test
    void patchDevice_WhenDeviceInUse_ShouldThrowException() {
        // Given
        Device inUseDevice = Device.createWithIdAndTime(testId, "In Use Device", Brand.SAMSUNG, State.IN_USE, testTime);
        Device patchDevice = Device.createNew("Patched Device", Brand.GOOGLE, State.INACTIVE);
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.patchDevice(testId, patchDevice))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("is still in use so name and and cannot be updated");
        
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).save(any());
    }

    @Test
    void deleteDevice_WhenDeviceExists_ShouldDeleteDevice() {
        // Given
        Device availableDevice = Device.createWithIdAndTime(testId, "Available Device", Brand.SAMSUNG, State.AVAILABLE, testTime);
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(availableDevice));

        // When
        deviceService.deleteDevice(testId);

        // Then
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort).deleteById(testId);
    }

    @Test
    void deleteDevice_WhenDeviceNotExists_ShouldThrowException() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deviceService.deleteDevice(testId))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Device with id '" + testId + "' not found");
        
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).deleteById(any());
    }
}