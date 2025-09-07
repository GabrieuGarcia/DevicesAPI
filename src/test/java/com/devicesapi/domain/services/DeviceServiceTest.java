package com.devicesapi.domain.services;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.exception.DeviceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
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
        testDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );
    }

    @Test
    void createDevice_ShouldSaveDevice() {
        // When
        deviceService.createDevice(testDevice);

        // Then
        verify(devicePersistencePort).save(testDevice);
    }

    @Test
    void getDeviceById_ShouldReturnDevice_WhenDeviceExists() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));

        // When
        Optional<Device> result = deviceService.getDeviceById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDevice.getId(), result.get().getId());
        assertEquals(testDevice.getName(), result.get().getName());
        assertEquals(testDevice.getBrand(), result.get().getBrand());
        assertEquals(testDevice.getState(), result.get().getState());
        verify(devicePersistencePort).findById(testId);
    }

    @Test
    void getDeviceById_ShouldReturnEmpty_WhenDeviceNotFound() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When
        Optional<Device> result = deviceService.getDeviceById(testId);

        // Then
        assertTrue(result.isEmpty());
        verify(devicePersistencePort).findById(testId);
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() {
        // Given
        Device device2 = Device.createWithIdAndTime(
                UUID.randomUUID(),
                "Galaxy S24",
                Brand.SAMSUNG,
                State.IN_USE,
                testTime
        );
        List<Device> devices = Arrays.asList(testDevice, device2);
        when(devicePersistencePort.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceService.getAllDevices();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(devices, result);
        verify(devicePersistencePort).findAll();
    }

    @Test
    void getDevicesByBrand_ShouldReturnDevicesWithSpecificBrand() {
        // Given
        List<Device> appleDevices = Arrays.asList(testDevice);
        when(devicePersistencePort.findByBrand(Brand.APPLE)).thenReturn(appleDevices);

        // When
        List<Device> result = deviceService.getDevicesByBrand(Brand.APPLE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Brand.APPLE, result.get(0).getBrand());
        verify(devicePersistencePort).findByBrand(Brand.APPLE);
    }

    @Test
    void getDevicesByState_ShouldReturnDevicesWithSpecificState() {
        // Given
        List<Device> availableDevices = Arrays.asList(testDevice);
        when(devicePersistencePort.findByState(State.AVAILABLE)).thenReturn(availableDevices);

        // When
        List<Device> result = deviceService.getDevicesByState(State.AVAILABLE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(State.AVAILABLE, result.get(0).getState());
        verify(devicePersistencePort).findByState(State.AVAILABLE);
    }

    @Test
    void patchDevice_ShouldUpdateOnlyProvidedFields() {
        // Given
        Device patchDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15 Pro",
                null, // Brand not provided
                State.AVAILABLE,
                testTime
        );
        Device expectedDevice = Device.updateDevice(
                testId,
                "iPhone 15 Pro",
                Brand.APPLE, // Should keep original brand
                State.AVAILABLE,
                testTime
        );
        
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));
        when(devicePersistencePort.save(any(Device.class))).thenReturn(expectedDevice);

        // When
        deviceService.patchDevice(testId, patchDevice);

        // Then
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort).save(any(Device.class));
    }

    @Test
    void patchDevice_ShouldThrowException_WhenDeviceInUseAndNameOrBrandChanged() {
        // Given
        Device inUseDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.IN_USE,
                testTime
        );
        Device patchDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15 Pro", // Different name
                Brand.APPLE,
                State.IN_USE,
                testTime
        );
        
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        DeviceNotFoundException exception = assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.patchDevice(testId, patchDevice)
        );
        assertTrue(exception.getMessage().contains("is still in use so name and and cannot be updated"));
    }

    @Test
    void updateDevice_ShouldReturnUpdatedDevice() {
        // Given
        Device updatedDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15 Pro",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );
        Device expectedDevice = Device.updateDevice(
                testId,
                "iPhone 15 Pro",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );
        
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));
        when(devicePersistencePort.save(any(Device.class))).thenReturn(expectedDevice);

        // When
        deviceService.updateDevice(testId, updatedDevice);

        // Then
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort).save(any(Device.class));
    }

    @Test
    void updateDevice_ShouldThrowException_WhenDeviceNotFound() {
        // Given
        Device updatedDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15 Pro",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        DeviceNotFoundException exception = assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.updateDevice(testId, updatedDevice)
        );
        assertEquals("Device with id '" + testId + "' not found", exception.getMessage());
    }

    @Test
    void updateDevice_ShouldThrowException_WhenDeviceInUseAndNameOrBrandChanged() {
        // Given
        Device inUseDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.IN_USE,
                testTime
        );
        Device updatedDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15 Pro", // Different name
                Brand.APPLE,
                State.IN_USE,
                testTime
        );
        
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        DeviceNotFoundException exception = assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.updateDevice(testId, updatedDevice)
        );
        assertTrue(exception.getMessage().contains("is still in use so name and and cannot be updated"));
    }

    @Test
    void deleteDevice_ShouldDeleteDevice_WhenDeviceExistsAndNotInUse() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(testDevice));

        // When
        deviceService.deleteDevice(testId);

        // Then
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort).deleteById(testId);
    }

    @Test
    void deleteDevice_ShouldThrowException_WhenDeviceNotFound() {
        // Given
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        DeviceNotFoundException exception = assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.deleteDevice(testId)
        );
        assertEquals("Device with id '" + testId + "' not found", exception.getMessage());
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).deleteById(testId);
    }

    @Test
    void deleteDevice_ShouldThrowException_WhenDeviceInUse() {
        // Given
        Device inUseDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.IN_USE,
                testTime
        );
        when(devicePersistencePort.findById(testId)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        DeviceNotFoundException exception = assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.deleteDevice(testId)
        );
        assertEquals("Device with id '" + testId + "' is still in use and cannot be deleted", exception.getMessage());
        verify(devicePersistencePort).findById(testId);
        verify(devicePersistencePort, never()).deleteById(testId);
    }
}