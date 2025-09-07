package com.devicesapi.domain.services;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.ports.DevicePersistencePort;
import com.devicesapi.domain.ports.DeviceServicePort;
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
    private DevicePersistencePort devicePort;

    @InjectMocks
    private DeviceService deviceService;

    private DeviceServicePort deviceServicePort;

    private Device testDevice;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE,
                LocalDateTime.now()
        );
        deviceServicePort = deviceService;
    }

    @Test
    void createDevice_ShouldReturnDevice() {
        // Given
        when(devicePort.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceServicePort.createDevice(testDevice);

        // Then
        assertNotNull(result);
        assertEquals(testDevice.getName(), result.getName());
        verify(devicePort).save(testDevice);
    }

    @Test
    void getDeviceById_ShouldReturnDevice_WhenDeviceExists() {
        // Given
        when(devicePort.findById(testId)).thenReturn(Optional.of(testDevice));

        // When
        Optional<Device> result = deviceServicePort.getDeviceById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDevice, result.get());
        verify(devicePort).findById(testId);
    }

    @Test
    void getDeviceById_ShouldReturnEmpty_WhenDeviceDoesNotExist() {
        // Given
        when(devicePort.findById(testId)).thenReturn(Optional.empty());

        // When
        Optional<Device> result = deviceServicePort.getDeviceById(testId);

        // Then
        assertFalse(result.isPresent());
        verify(devicePort).findById(testId);
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(devicePort.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceServicePort.getAllDevices();

        // Then
        assertEquals(1, result.size());
        assertEquals(testDevice, result.get(0));
        verify(devicePort).findAll();
    }

    @Test
    void updateDevice_ShouldUpdateDevice_WhenDeviceExists() {
        // Given
        Device updatedDevice = Device.createNew(
                "iPhone 15 Pro",
                Brand.APPLE,
                State.IN_USE
        );
        
        when(devicePort.findById(testId)).thenReturn(Optional.of(testDevice));
        when(devicePort.save(any(Device.class))).thenReturn(updatedDevice);

        // When
        Device result = deviceServicePort.updateDevice(testId, updatedDevice);

        // Then
        assertNotNull(result);
        verify(devicePort).findById(testId);
        verify(devicePort).save(any(Device.class));
    }

    @Test
    void updateDevice_ShouldThrowException_WhenDeviceDoesNotExist() {
        // Given
        Device updatedDevice = Device.createNew(
                "iPhone 15 Pro",
                Brand.APPLE,
                State.IN_USE
        );
        
        when(devicePort.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deviceServicePort.updateDevice(testId, updatedDevice)
        );
        assertEquals("Device with id '" + testId + "' not found", exception.getMessage());
        verify(devicePort).findById(testId);
        verify(devicePort, never()).save(any());
    }

    @Test
    void deleteDevice_ShouldDeleteDevice_WhenDeviceExists() {
        // Given
        when(devicePort.existsById(testId)).thenReturn(true);

        // When
        deviceServicePort.deleteDevice(testId);

        // Then
        verify(devicePort).existsById(testId);
        verify(devicePort).deleteById(testId);
    }

    @Test
    void deleteDevice_ShouldThrowException_WhenDeviceDoesNotExist() {
        // Given
        when(devicePort.existsById(testId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deviceServicePort.deleteDevice(testId)
        );
        assertEquals("Device with id '" + testId + "' not found", exception.getMessage());
        verify(devicePort).existsById(testId);
        verify(devicePort, never()).deleteById(any());
    }
}