package com.devicesapi.application.dto;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceResponseDtoTest {

    private UUID testId;
    private String testName;
    private Brand testBrand;
    private State testState;
    private LocalDateTime testTime;
    private Device testDevice;
    private DeviceResponseDto testDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testName = "Test Device";
        testBrand = Brand.SAMSUNG;
        testState = State.AVAILABLE;
        testTime = LocalDateTime.now();
        
        testDevice = Device.createWithIdAndTime(testId, testName, testBrand, testState, testTime);
        testDto = new DeviceResponseDto(testId, testName, testBrand, testState, testTime);
    }

    @Test
    void constructor_ShouldCreateDtoWithAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "iPhone 15";
        Brand brand = Brand.APPLE;
        State state = State.IN_USE;
        LocalDateTime creationTime = LocalDateTime.now();

        // When
        DeviceResponseDto dto = new DeviceResponseDto(id, name, brand, state, creationTime);

        // Then
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.brand()).isEqualTo(brand);
        assertThat(dto.state()).isEqualTo(state);
        assertThat(dto.creationTime()).isEqualTo(creationTime);
    }

    @Test
    void fromDomain_ShouldConvertDeviceToDto() {
        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(testDevice);

        // Then
        assertThat(dto.id()).isEqualTo(testDevice.getId());
        assertThat(dto.name()).isEqualTo(testDevice.getName());
        assertThat(dto.brand()).isEqualTo(testDevice.getBrand());
        assertThat(dto.state()).isEqualTo(testDevice.getState());
        assertThat(dto.creationTime()).isEqualTo(testDevice.getCreationTime());
    }

    @Test
    void fromDomain_WithNullId_ShouldConvertCorrectly() {
        // Given
        Device deviceWithNullId = Device.createNew(testName, testBrand, testState);

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(deviceWithNullId);

        // Then
        assertThat(dto.id()).isNull();
        assertThat(dto.name()).isEqualTo(deviceWithNullId.getName());
        assertThat(dto.brand()).isEqualTo(deviceWithNullId.getBrand());
        assertThat(dto.state()).isEqualTo(deviceWithNullId.getState());
        assertThat(dto.creationTime()).isEqualTo(deviceWithNullId.getCreationTime());
    }

    @Test
    void testAllBrands_ShouldWorkWithAllEnumValues() {
        // Test all brand enum values
        for (Brand brand : Brand.values()) {
            DeviceResponseDto dto = new DeviceResponseDto(testId, testName, brand, testState, testTime);
            assertThat(dto.brand()).isEqualTo(brand);
        }
    }

    @Test
    void testAllStates_ShouldWorkWithAllEnumValues() {
        // Test all state enum values
        for (State state : State.values()) {
            DeviceResponseDto dto = new DeviceResponseDto(testId, testName, testBrand, state, testTime);
            assertThat(dto.state()).isEqualTo(state);
        }
    }

    @Test
    void equals_WithSameValues_ShouldBeEqual() {
        // Given
        DeviceResponseDto dto1 = new DeviceResponseDto(testId, testName, testBrand, testState, testTime);
        DeviceResponseDto dto2 = new DeviceResponseDto(testId, testName, testBrand, testState, testTime);

        // Then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void equals_WithDifferentValues_ShouldNotBeEqual() {
        // Given
        DeviceResponseDto dto1 = new DeviceResponseDto(testId, testName, testBrand, testState, testTime);
        DeviceResponseDto dto2 = new DeviceResponseDto(UUID.randomUUID(), "Different Device", Brand.APPLE, State.IN_USE, LocalDateTime.now());

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        // When
        String result = testDto.toString();

        // Then
        assertThat(result).contains(testId.toString());
        assertThat(result).contains("Test Device");
        assertThat(result).contains("SAMSUNG");
        assertThat(result).contains("AVAILABLE");
        assertThat(result).contains(testTime.toString());
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        // Then
        assertThat(testDto.id()).isEqualTo(testId);
        assertThat(testDto.name()).isEqualTo(testName);
        assertThat(testDto.brand()).isEqualTo(testBrand);
        assertThat(testDto.state()).isEqualTo(testState);
        assertThat(testDto.creationTime()).isEqualTo(testTime);
    }

    @Test
    void fromDomain_WithDifferentBrandsAndStates_ShouldConvertCorrectly() {
        // Test conversion with different combinations
        for (Brand brand : Brand.values()) {
            for (State state : State.values()) {
                Device device = Device.createWithIdAndTime(testId, testName, brand, state, testTime);
                DeviceResponseDto dto = DeviceResponseDto.fromDomain(device);
                
                assertThat(dto.brand()).isEqualTo(brand);
                assertThat(dto.state()).isEqualTo(state);
                assertThat(dto.id()).isEqualTo(testId);
                assertThat(dto.name()).isEqualTo(testName);
                assertThat(dto.creationTime()).isEqualTo(testTime);
            }
        }
    }

    @Test
    void fromDomain_WithNullFields_ShouldHandleGracefully() {
        // Given - Device with null ID (which is valid for new devices)
        Device deviceWithNullId = Device.createNew("New Device", Brand.GOOGLE, State.AVAILABLE);

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(deviceWithNullId);

        // Then
        assertThat(dto.id()).isNull();
        assertThat(dto.name()).isEqualTo("New Device");
        assertThat(dto.brand()).isEqualTo(Brand.GOOGLE);
        assertThat(dto.state()).isEqualTo(State.AVAILABLE);
        assertThat(dto.creationTime()).isNotNull();
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        // Given
        Device originalDevice = Device.createWithIdAndTime(testId, testName, testBrand, testState, testTime);

        // When
        DeviceResponseDto dto = DeviceResponseDto.fromDomain(originalDevice);

        // Then - All data should be preserved
        assertThat(dto.id()).isEqualTo(originalDevice.getId());
        assertThat(dto.name()).isEqualTo(originalDevice.getName());
        assertThat(dto.brand()).isEqualTo(originalDevice.getBrand());
        assertThat(dto.state()).isEqualTo(originalDevice.getState());
        assertThat(dto.creationTime()).isEqualTo(originalDevice.getCreationTime());
    }
}