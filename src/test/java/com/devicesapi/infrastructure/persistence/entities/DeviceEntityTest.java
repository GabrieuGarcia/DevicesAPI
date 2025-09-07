package com.devicesapi.infrastructure.persistence.entities;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceEntityTest {

    private UUID testId;
    private LocalDateTime testTime;
    private Device testDevice;

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
    void constructor_ShouldCreateEntityWithAllFields() {
        // When
        DeviceEntity entity = new DeviceEntity(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );

        // Then
        assertEquals(testId, entity.getId());
        assertEquals("iPhone 15", entity.getName());
        assertEquals(Brand.APPLE, entity.getBrand());
        assertEquals(State.AVAILABLE, entity.getState());
        assertEquals(testTime, entity.getCreationTime());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyEntity() {
        // When
        DeviceEntity entity = new DeviceEntity();

        // Then
        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getBrand());
        assertNull(entity.getState());
        assertNull(entity.getCreationTime());
    }

    @Test
    void createWithTime_ShouldCreateEntityWithNullId() {
        // When
        DeviceEntity entity = DeviceEntity.createWithTime(
                "MacBook Pro",
                Brand.APPLE,
                State.IN_USE,
                testTime
        );

        // Then
        assertNull(entity.getId());
        assertEquals("MacBook Pro", entity.getName());
        assertEquals(Brand.APPLE, entity.getBrand());
        assertEquals(State.IN_USE, entity.getState());
        assertEquals(testTime, entity.getCreationTime());
    }

    @Test
    void fromDomain_ShouldConvertDeviceToEntity() {
        // When
        DeviceEntity entity = DeviceEntity.fromDomain(testDevice);

        // Then
        assertEquals(testDevice.getId(), entity.getId());
        assertEquals(testDevice.getName(), entity.getName());
        assertEquals(testDevice.getBrand(), entity.getBrand());
        assertEquals(testDevice.getState(), entity.getState());
        assertEquals(testDevice.getCreationTime(), entity.getCreationTime());
    }

    @Test
    void fromDomain_ShouldHandleNewDevice() {
        // Given
        Device newDevice = Device.createNew("Galaxy S24", Brand.SAMSUNG, State.AVAILABLE);

        // When
        DeviceEntity entity = DeviceEntity.fromDomain(newDevice);

        // Then
        assertEquals(newDevice.getId(), entity.getId());
        assertEquals("Galaxy S24", entity.getName());
        assertEquals(Brand.SAMSUNG, entity.getBrand());
        assertEquals(State.AVAILABLE, entity.getState());
        assertEquals(newDevice.getCreationTime(), entity.getCreationTime());
    }

    @Test
    void toDomain_ShouldConvertEntityToDevice() {
        // Given
        DeviceEntity entity = new DeviceEntity(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );

        // When
        Device device = entity.toDomain();

        // Then
        assertEquals(entity.getId(), device.getId());
        assertEquals(entity.getName(), device.getName());
        assertEquals(entity.getBrand(), device.getBrand());
        assertEquals(entity.getState(), device.getState());
        assertEquals(entity.getCreationTime(), device.getCreationTime());
    }

    @Test
    void toDomain_ShouldHandleAllBrands() {
        // Test APPLE
        DeviceEntity appleEntity = new DeviceEntity(
                testId, "iPhone", Brand.APPLE, State.AVAILABLE, testTime
        );
        Device appleDevice = appleEntity.toDomain();
        assertEquals(Brand.APPLE, appleDevice.getBrand());

        // Test SAMSUNG
        DeviceEntity samsungEntity = new DeviceEntity(
                testId, "Galaxy", Brand.SAMSUNG, State.AVAILABLE, testTime
        );
        Device samsungDevice = samsungEntity.toDomain();
        assertEquals(Brand.SAMSUNG, samsungDevice.getBrand());

        // Test GOOGLE
        DeviceEntity googleEntity = new DeviceEntity(
                testId, "Pixel", Brand.GOOGLE, State.AVAILABLE, testTime
        );
        Device googleDevice = googleEntity.toDomain();
        assertEquals(Brand.GOOGLE, googleDevice.getBrand());

        // Test XIAOMI
        DeviceEntity xiaomiEntity = new DeviceEntity(
                testId, "Mi 13", Brand.XIAOMI, State.AVAILABLE, testTime
        );
        Device xiaomiDevice = xiaomiEntity.toDomain();
        assertEquals(Brand.XIAOMI, xiaomiDevice.getBrand());
    }

    @Test
    void toDomain_ShouldHandleAllStates() {
        // Test AVAILABLE
        DeviceEntity availableEntity = new DeviceEntity(
                testId, "iPhone", Brand.APPLE, State.AVAILABLE, testTime
        );
        Device availableDevice = availableEntity.toDomain();
        assertEquals(State.AVAILABLE, availableDevice.getState());

        // Test IN_USE
        DeviceEntity inUseEntity = new DeviceEntity(
                testId, "iPhone", Brand.APPLE, State.IN_USE, testTime
        );
        Device inUseDevice = inUseEntity.toDomain();
        assertEquals(State.IN_USE, inUseDevice.getState());

        // Test INACTIVE
        DeviceEntity inactiveEntity = new DeviceEntity(
                testId, "iPhone", Brand.APPLE, State.INACTIVE, testTime
        );
        Device inactiveDevice = inactiveEntity.toDomain();
        assertEquals(State.INACTIVE, inactiveDevice.getState());
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        // Given
        Device originalDevice = Device.createWithIdAndTime(
                testId,
                "MacBook Air",
                Brand.APPLE,
                State.IN_USE,
                testTime
        );

        // When - Convert to entity and back to domain
        DeviceEntity entity = DeviceEntity.fromDomain(originalDevice);
        Device convertedDevice = entity.toDomain();

        // Then
        assertEquals(originalDevice.getId(), convertedDevice.getId());
        assertEquals(originalDevice.getName(), convertedDevice.getName());
        assertEquals(originalDevice.getBrand(), convertedDevice.getBrand());
        assertEquals(originalDevice.getState(), convertedDevice.getState());
        assertEquals(originalDevice.getCreationTime(), convertedDevice.getCreationTime());
    }

    @Test
    void fromDomain_ShouldHandleNullId() {
        // Given
        Device deviceWithNullId = Device.createNew("Test Device", Brand.SAMSUNG, State.AVAILABLE);
        // Assuming createNew might create device with null ID initially

        // When
        DeviceEntity entity = DeviceEntity.fromDomain(deviceWithNullId);

        // Then
        assertEquals(deviceWithNullId.getId(), entity.getId());
        assertEquals("Test Device", entity.getName());
        assertEquals(Brand.SAMSUNG, entity.getBrand());
        assertEquals(State.AVAILABLE, entity.getState());
        assertNotNull(entity.getCreationTime());
    }

    @Test
    void createWithTime_ShouldHandleAllBrandsAndStates() {
        // Test with different combinations
        DeviceEntity entity1 = DeviceEntity.createWithTime(
                "Pixel 8", Brand.GOOGLE, State.INACTIVE, testTime
        );
        assertEquals("Pixel 8", entity1.getName());
        assertEquals(Brand.GOOGLE, entity1.getBrand());
        assertEquals(State.INACTIVE, entity1.getState());

        DeviceEntity entity2 = DeviceEntity.createWithTime(
                "Mi 13 Pro", Brand.XIAOMI, State.IN_USE, testTime
        );
        assertEquals("Mi 13 Pro", entity2.getName());
        assertEquals(Brand.XIAOMI, entity2.getBrand());
        assertEquals(State.IN_USE, entity2.getState());
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // Given
        DeviceEntity entity = new DeviceEntity(
                testId,
                "Test Device",
                Brand.SAMSUNG,
                State.INACTIVE,
                testTime
        );

        // When & Then
        assertEquals(testId, entity.getId());
        assertEquals("Test Device", entity.getName());
        assertEquals(Brand.SAMSUNG, entity.getBrand());
        assertEquals(State.INACTIVE, entity.getState());
        assertEquals(testTime, entity.getCreationTime());
    }
}