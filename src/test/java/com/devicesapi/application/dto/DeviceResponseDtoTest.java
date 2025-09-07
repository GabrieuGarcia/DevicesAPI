package com.devicesapi.application.dto;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceResponseDtoTest {

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
    void constructor_ShouldCreateDtoWithAllFields() {
        // When
        DeviceResponseDto dto = new DeviceResponseDto(
                testId,
                "Galaxy S24",
                Brand.SAMSUNG,
                State.IN_USE,
                testTime
        );

        // Then
        assertEquals(testId, dto.id());
        assertEquals("Galaxy S24", dto.name());
        assertEquals(Brand.SAMSUNG, dto.brand());
        assertEquals(State.IN_USE, dto.state());
        assertEquals(testTime, dto.creationTime());
    }

    @Test
    void fromDomain_ShouldConvertDeviceToDto() {
        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(testDevice);

        // Then
        assertNotNull(dto);
        assertEquals(testDevice.getId(), dto.id());
        assertEquals(testDevice.getName(), dto.name());
        assertEquals(testDevice.getBrand(), dto.brand());
        assertEquals(testDevice.getState(), dto.state());
        assertEquals(testDevice.getCreationTime(), dto.creationTime());
    }

    @Test
    void fromDomain_ShouldHandleNewDevice() {
        // Given
        Device newDevice = Device.createNew("Pixel 8", Brand.GOOGLE, State.AVAILABLE);

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(newDevice);

        // Then
        assertEquals(newDevice.getId(), dto.id());
        assertEquals("Pixel 8", dto.name());
        assertEquals(Brand.GOOGLE, dto.brand());
        assertEquals(State.AVAILABLE, dto.state());
        assertEquals(newDevice.getCreationTime(), dto.creationTime());
    }

    @Test
    void fromDomain_ShouldHandleAllBrands() {
        // Test APPLE
        Device appleDevice = Device.createWithIdAndTime(
                testId, "iPhone", Brand.APPLE, State.AVAILABLE, testTime
        );
        DeviceResponseDto appleDto = DeviceResponseDto.fromDomain(appleDevice);
        assertEquals(Brand.APPLE, appleDto.brand());

        // Test SAMSUNG
        Device samsungDevice = Device.createWithIdAndTime(
                testId, "Galaxy", Brand.SAMSUNG, State.AVAILABLE, testTime
        );
        DeviceResponseDto samsungDto = DeviceResponseDto.fromDomain(samsungDevice);
        assertEquals(Brand.SAMSUNG, samsungDto.brand());

        // Test GOOGLE
        Device googleDevice = Device.createWithIdAndTime(
                testId, "Pixel", Brand.GOOGLE, State.AVAILABLE, testTime
        );
        DeviceResponseDto googleDto = DeviceResponseDto.fromDomain(googleDevice);
        assertEquals(Brand.GOOGLE, googleDto.brand());

        // Test XIAOMI
        Device xiaomiDevice = Device.createWithIdAndTime(
                testId, "Mi 13", Brand.XIAOMI, State.AVAILABLE, testTime
        );
        DeviceResponseDto xiaomiDto = DeviceResponseDto.fromDomain(xiaomiDevice);
        assertEquals(Brand.XIAOMI, xiaomiDto.brand());
    }

    @Test
    void fromDomain_ShouldHandleAllStates() {
        // Test AVAILABLE
        Device availableDevice = Device.createWithIdAndTime(
                testId, "iPhone", Brand.APPLE, State.AVAILABLE, testTime
        );
        DeviceResponseDto availableDto = DeviceResponseDto.fromDomain(availableDevice);
        assertEquals(State.AVAILABLE, availableDto.state());

        // Test IN_USE
        Device inUseDevice = Device.createWithIdAndTime(
                testId, "iPhone", Brand.APPLE, State.IN_USE, testTime
        );
        DeviceResponseDto inUseDto = DeviceResponseDto.fromDomain(inUseDevice);
        assertEquals(State.IN_USE, inUseDto.state());

        // Test INACTIVE
        Device inactiveDevice = Device.createWithIdAndTime(
                testId, "iPhone", Brand.APPLE, State.INACTIVE, testTime
        );
        DeviceResponseDto inactiveDto = DeviceResponseDto.fromDomain(inactiveDevice);
        assertEquals(State.INACTIVE, inactiveDto.state());
    }

    @Test
    void fromDomain_ShouldPreserveAllDeviceData() {
        // Given
        Device complexDevice = Device.createWithIdAndTime(
                UUID.randomUUID(),
                "MacBook Pro 16-inch",
                Brand.APPLE,
                State.IN_USE,
                LocalDateTime.of(2024, 1, 15, 10, 30, 45)
        );

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(complexDevice);

        // Then
        assertEquals(complexDevice.getId(), dto.id());
        assertEquals("MacBook Pro 16-inch", dto.name());
        assertEquals(Brand.APPLE, dto.brand());
        assertEquals(State.IN_USE, dto.state());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 45), dto.creationTime());
    }

    @Test
    void fromDomain_ShouldHandleNullId() {
        // Given
        Device deviceWithNullId = Device.createNew("Test Device", Brand.SAMSUNG, State.AVAILABLE);
        // Assuming createNew might create device with null ID initially

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(deviceWithNullId);

        // Then
        assertEquals(deviceWithNullId.getId(), dto.id());
        assertEquals("Test Device", dto.name());
        assertEquals(Brand.SAMSUNG, dto.brand());
        assertEquals(State.AVAILABLE, dto.state());
        assertNotNull(dto.creationTime());
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // Given
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(testDevice);

        // When & Then
        assertEquals(testId, dto.id());
        assertEquals("iPhone 15", dto.name());
        assertEquals(Brand.APPLE, dto.brand());
        assertEquals(State.AVAILABLE, dto.state());
        assertEquals(testTime, dto.creationTime());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Given
        DeviceResponseDto dto1 = new DeviceResponseDto(
                testId, "iPhone 15", Brand.APPLE, State.AVAILABLE, testTime
        );
        DeviceResponseDto dto2 = new DeviceResponseDto(
                testId, "iPhone 15", Brand.APPLE, State.AVAILABLE, testTime
        );
        DeviceResponseDto dto3 = new DeviceResponseDto(
                UUID.randomUUID(), "Galaxy S24", Brand.SAMSUNG, State.IN_USE, testTime
        );

        // When & Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Given
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(testDevice);

        // When
        String result = dto.toString();

        // Then
        assertTrue(result.contains("iPhone 15"));
        assertTrue(result.contains("APPLE"));
        assertTrue(result.contains("AVAILABLE"));
        assertTrue(result.contains(testId.toString()));
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        // Given
        Device originalDevice = Device.createWithIdAndTime(
                testId,
                "iPad Air",
                Brand.APPLE,
                State.INACTIVE,
                testTime
        );

        // When - Convert to DTO
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(originalDevice);

        // Then - Verify all data is preserved
        assertEquals(originalDevice.getId(), dto.id());
        assertEquals(originalDevice.getName(), dto.name());
        assertEquals(originalDevice.getBrand(), dto.brand());
        assertEquals(originalDevice.getState(), dto.state());
        assertEquals(originalDevice.getCreationTime(), dto.creationTime());
    }

    @Test
    void fromDomain_ShouldHandleDifferentTimeFormats() {
        // Given
        LocalDateTime specificTime = LocalDateTime.of(2023, 12, 25, 14, 30, 0);
        Device deviceWithSpecificTime = Device.createWithIdAndTime(
                testId,
                "Christmas Device",
                Brand.GOOGLE,
                State.AVAILABLE,
                specificTime
        );

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(deviceWithSpecificTime);

        // Then
        assertEquals(specificTime, dto.creationTime());
        assertEquals(specificTime.getYear(), dto.creationTime().getYear());
        assertEquals(specificTime.getMonth(), dto.creationTime().getMonth());
        assertEquals(specificTime.getDayOfMonth(), dto.creationTime().getDayOfMonth());
        assertEquals(specificTime.getHour(), dto.creationTime().getHour());
        assertEquals(specificTime.getMinute(), dto.creationTime().getMinute());
    }
}