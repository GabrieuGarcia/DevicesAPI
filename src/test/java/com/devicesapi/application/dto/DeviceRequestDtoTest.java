package com.devicesapi.application.dto;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceRequestDtoTest {

    private DeviceRequestDto deviceRequestDto;
    private Device existingDevice;
    private UUID testId;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testTime = LocalDateTime.now();
        deviceRequestDto = new DeviceRequestDto(
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE
        );
        existingDevice = Device.createWithIdAndTime(
                testId,
                "MacBook Pro",
                Brand.APPLE,
                State.IN_USE,
                testTime
        );
    }

    @Test
    void constructor_ShouldCreateDtoWithAllFields() {
        // When
        DeviceRequestDto dto = new DeviceRequestDto(
                "Galaxy S24",
                Brand.SAMSUNG,
                State.IN_USE
        );

        // Then
        assertEquals("Galaxy S24", dto.name());
        assertEquals(Brand.SAMSUNG, dto.brand());
        assertEquals(State.IN_USE, dto.state());
    }

    @Test
    void toDomain_ShouldCreateNewDevice() {
        // When
        Device device = deviceRequestDto.toDomain();

        // Then
        assertNotNull(device);
        assertEquals("iPhone 15", device.getName());
        assertEquals(Brand.APPLE, device.getBrand());
        assertEquals(State.AVAILABLE, device.getState());
        assertNotNull(device.getCreationTime());
        // ID should be null for new devices
        assertNull(device.getId());
    }

    @Test
    void toDomain_ShouldCreateDeviceWithCurrentTime() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        
        // When
        Device device = deviceRequestDto.toDomain();
        
        // Then
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertNotNull(device.getCreationTime());
        assertTrue(device.getCreationTime().isAfter(beforeCreation));
        assertTrue(device.getCreationTime().isBefore(afterCreation));
    }

    @Test
    void toDomainWithExistingDevice_ShouldPreserveIdAndCreationTime() {
        // When
        Device device = deviceRequestDto.toDomain(existingDevice);

        // Then
        assertNotNull(device);
        assertEquals(testId, device.getId());
        assertEquals("iPhone 15", device.getName());
        assertEquals(Brand.APPLE, device.getBrand());
        assertEquals(State.AVAILABLE, device.getState());
        assertEquals(testTime, device.getCreationTime());
    }

    @Test
    void toDomainWithExistingDevice_ShouldOverrideAllFields() {
        // Given
        DeviceRequestDto updateDto = new DeviceRequestDto(
                "iPad Pro",
                Brand.APPLE,
                State.INACTIVE
        );

        // When
        Device device = updateDto.toDomain(existingDevice);

        // Then
        assertEquals(testId, device.getId());
        assertEquals("iPad Pro", device.getName());
        assertEquals(Brand.APPLE, device.getBrand());
        assertEquals(State.INACTIVE, device.getState());
        assertEquals(testTime, device.getCreationTime());
    }

    @Test
    void toDomain_ShouldHandleAllBrands() {
        // Test APPLE
        DeviceRequestDto appleDto = new DeviceRequestDto("iPhone", Brand.APPLE, State.AVAILABLE);
        Device appleDevice = appleDto.toDomain();
        assertEquals(Brand.APPLE, appleDevice.getBrand());

        // Test SAMSUNG
        DeviceRequestDto samsungDto = new DeviceRequestDto("Galaxy", Brand.SAMSUNG, State.AVAILABLE);
        Device samsungDevice = samsungDto.toDomain();
        assertEquals(Brand.SAMSUNG, samsungDevice.getBrand());

        // Test GOOGLE
        DeviceRequestDto googleDto = new DeviceRequestDto("Pixel", Brand.GOOGLE, State.AVAILABLE);
        Device googleDevice = googleDto.toDomain();
        assertEquals(Brand.GOOGLE, googleDevice.getBrand());

        // Test XIAOMI
        DeviceRequestDto xiaomiDto = new DeviceRequestDto("Mi 13", Brand.XIAOMI, State.AVAILABLE);
        Device xiaomiDevice = xiaomiDto.toDomain();
        assertEquals(Brand.XIAOMI, xiaomiDevice.getBrand());
    }

    @Test
    void toDomain_ShouldHandleAllStates() {
        // Test AVAILABLE
        DeviceRequestDto availableDto = new DeviceRequestDto("iPhone", Brand.APPLE, State.AVAILABLE);
        Device availableDevice = availableDto.toDomain();
        assertEquals(State.AVAILABLE, availableDevice.getState());

        // Test IN_USE
        DeviceRequestDto inUseDto = new DeviceRequestDto("iPhone", Brand.APPLE, State.IN_USE);
        Device inUseDevice = inUseDto.toDomain();
        assertEquals(State.IN_USE, inUseDevice.getState());

        // Test INACTIVE
        DeviceRequestDto inactiveDto = new DeviceRequestDto("iPhone", Brand.APPLE, State.INACTIVE);
        Device inactiveDevice = inactiveDto.toDomain();
        assertEquals(State.INACTIVE, inactiveDevice.getState());
    }

    @Test
    void toDomainWithExistingDevice_ShouldHandleDifferentBrands() {
        // Given
        DeviceRequestDto samsungDto = new DeviceRequestDto(
                "Galaxy Note",
                Brand.SAMSUNG,
                State.AVAILABLE
        );

        // When
        Device device = samsungDto.toDomain(existingDevice);

        // Then
        assertEquals(testId, device.getId());
        assertEquals("Galaxy Note", device.getName());
        assertEquals(Brand.SAMSUNG, device.getBrand());
        assertEquals(State.AVAILABLE, device.getState());
        assertEquals(testTime, device.getCreationTime());
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // When & Then
        assertEquals("iPhone 15", deviceRequestDto.name());
        assertEquals(Brand.APPLE, deviceRequestDto.brand());
        assertEquals(State.AVAILABLE, deviceRequestDto.state());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Given
        DeviceRequestDto dto1 = new DeviceRequestDto("iPhone 15", Brand.APPLE, State.AVAILABLE);
        DeviceRequestDto dto2 = new DeviceRequestDto("iPhone 15", Brand.APPLE, State.AVAILABLE);
        DeviceRequestDto dto3 = new DeviceRequestDto("Galaxy S24", Brand.SAMSUNG, State.IN_USE);

        // When & Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // When
        String result = deviceRequestDto.toString();

        // Then
        assertTrue(result.contains("iPhone 15"));
        assertTrue(result.contains("APPLE"));
        assertTrue(result.contains("AVAILABLE"));
    }
}