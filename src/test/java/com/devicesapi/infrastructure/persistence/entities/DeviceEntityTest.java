package com.devicesapi.infrastructure.persistence.entities;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceEntityTest {

    private UUID testId;
    private String testName;
    private Brand testBrand;
    private State testState;
    private LocalDateTime testTime;
    private Device testDevice;
    private DeviceEntity testEntity;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testName = "Test Device";
        testBrand = Brand.SAMSUNG;
        testState = State.AVAILABLE;
        testTime = LocalDateTime.now();
        
        testDevice = Device.createWithIdAndTime(testId, testName, testBrand, testState, testTime);
        testEntity = new DeviceEntity(testId, testName, testBrand, testState, testTime);
    }

    @Test
    void constructor_ShouldCreateEntityWithAllFields() {
        // When
        DeviceEntity entity = new DeviceEntity(testId, testName, testBrand, testState, testTime);

        // Then
        assertThat(entity.getId()).isEqualTo(testId);
        assertThat(entity.getName()).isEqualTo(testName);
        assertThat(entity.getBrand()).isEqualTo(testBrand);
        assertThat(entity.getState()).isEqualTo(testState);
        assertThat(entity.getCreationTime()).isEqualTo(testTime);
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyEntity() {
        // When
        DeviceEntity entity = new DeviceEntity();

        // Then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isNull();
        assertThat(entity.getBrand()).isNull();
        assertThat(entity.getState()).isNull();
        assertThat(entity.getCreationTime()).isNull();
    }

    @Test
    void createWithTime_ShouldCreateEntityWithNullId() {
        // When
        DeviceEntity entity = DeviceEntity.createWithTime(testName, testBrand, testState, testTime);

        // Then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(testName);
        assertThat(entity.getBrand()).isEqualTo(testBrand);
        assertThat(entity.getState()).isEqualTo(testState);
        assertThat(entity.getCreationTime()).isEqualTo(testTime);
    }

    @Test
    void fromDomain_ShouldConvertDeviceToEntity() {
        // When
        DeviceEntity entity = DeviceEntity.fromDomain(testDevice);

        // Then
        assertThat(entity.getId()).isEqualTo(testDevice.getId());
        assertThat(entity.getName()).isEqualTo(testDevice.getName());
        assertThat(entity.getBrand()).isEqualTo(testDevice.getBrand());
        assertThat(entity.getState()).isEqualTo(testDevice.getState());
        assertThat(entity.getCreationTime()).isEqualTo(testDevice.getCreationTime());
    }

    @Test
    void fromDomain_WithNullId_ShouldConvertCorrectly() {
        // Given
        Device deviceWithNullId = Device.createNew(testName, testBrand, testState);

        // When
        DeviceEntity entity = DeviceEntity.fromDomain(deviceWithNullId);

        // Then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(deviceWithNullId.getName());
        assertThat(entity.getBrand()).isEqualTo(deviceWithNullId.getBrand());
        assertThat(entity.getState()).isEqualTo(deviceWithNullId.getState());
        assertThat(entity.getCreationTime()).isEqualTo(deviceWithNullId.getCreationTime());
    }

    @Test
    void toDomain_ShouldConvertEntityToDevice() {
        // When
        Device device = testEntity.toDomain();

        // Then
        assertThat(device.getId()).isEqualTo(testEntity.getId());
        assertThat(device.getName()).isEqualTo(testEntity.getName());
        assertThat(device.getBrand()).isEqualTo(testEntity.getBrand());
        assertThat(device.getState()).isEqualTo(testEntity.getState());
        assertThat(device.getCreationTime()).isEqualTo(testEntity.getCreationTime());
    }

    @Test
    void toDomain_WithNullId_ShouldConvertCorrectly() {
        // Given
        DeviceEntity entityWithNullId = new DeviceEntity(null, testName, testBrand, testState, testTime);

        // When
        Device device = entityWithNullId.toDomain();

        // Then
        assertThat(device.getId()).isNull();
        assertThat(device.getName()).isEqualTo(testName);
        assertThat(device.getBrand()).isEqualTo(testBrand);
        assertThat(device.getState()).isEqualTo(testState);
        assertThat(device.getCreationTime()).isEqualTo(testTime);
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        // Given
        Device originalDevice = Device.createWithIdAndTime(testId, testName, testBrand, testState, testTime);

        // When
        DeviceEntity entity = DeviceEntity.fromDomain(originalDevice);
        Device convertedDevice = entity.toDomain();

        // Then
        assertThat(convertedDevice.getId()).isEqualTo(originalDevice.getId());
        assertThat(convertedDevice.getName()).isEqualTo(originalDevice.getName());
        assertThat(convertedDevice.getBrand()).isEqualTo(originalDevice.getBrand());
        assertThat(convertedDevice.getState()).isEqualTo(originalDevice.getState());
        assertThat(convertedDevice.getCreationTime()).isEqualTo(originalDevice.getCreationTime());
    }

    @Test
    void testAllBrands_ShouldWorkWithAllEnumValues() {
        // Test all brand enum values
        for (Brand brand : Brand.values()) {
            DeviceEntity entity = new DeviceEntity(testId, testName, brand, testState, testTime);
            assertThat(entity.getBrand()).isEqualTo(brand);
            
            Device device = entity.toDomain();
            assertThat(device.getBrand()).isEqualTo(brand);
        }
    }

    @Test
    void testAllStates_ShouldWorkWithAllEnumValues() {
        // Test all state enum values
        for (State state : State.values()) {
            DeviceEntity entity = new DeviceEntity(testId, testName, testBrand, state, testTime);
            assertThat(entity.getState()).isEqualTo(state);
            
            Device device = entity.toDomain();
            assertThat(device.getState()).isEqualTo(state);
        }
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // Then
        assertThat(testEntity.getId()).isEqualTo(testId);
        assertThat(testEntity.getName()).isEqualTo(testName);
        assertThat(testEntity.getBrand()).isEqualTo(testBrand);
        assertThat(testEntity.getState()).isEqualTo(testState);
        assertThat(testEntity.getCreationTime()).isEqualTo(testTime);
    }
}