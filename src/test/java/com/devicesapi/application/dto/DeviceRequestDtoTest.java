package com.devicesapi.application.dto;

import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceRequestDtoTest {

    private Validator validator;
    private DeviceRequestDto validDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        validDto = new DeviceRequestDto("Test Device", Brand.SAMSUNG, State.AVAILABLE);
    }

    @Test
    void constructor_ShouldCreateDtoWithAllFields() {
        // Given
        String name = "iPhone 15";
        Brand brand = Brand.APPLE;
        State state = State.IN_USE;

        // When
        DeviceRequestDto dto = new DeviceRequestDto(name, brand, state);

        // Then
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.brand()).isEqualTo(brand);
        assertThat(dto.state()).isEqualTo(state);
    }

    @Test
    void testAllBrands_ShouldWorkWithAllEnumValues() {
        // Test all brand enum values
        for (Brand brand : Brand.values()) {
            DeviceRequestDto dto = new DeviceRequestDto("Test Device", brand, State.AVAILABLE);
            assertThat(dto.brand()).isEqualTo(brand);
            
            Set<ConstraintViolation<DeviceRequestDto>> violations = validator.validate(dto);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void testAllStates_ShouldWorkWithAllEnumValues() {
        // Test all state enum values
        for (State state : State.values()) {
            DeviceRequestDto dto = new DeviceRequestDto("Test Device", Brand.SAMSUNG, state);
            assertThat(dto.state()).isEqualTo(state);
            
            Set<ConstraintViolation<DeviceRequestDto>> violations = validator.validate(dto);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void equals_WithSameValues_ShouldBeEqual() {
        // Given
        DeviceRequestDto dto1 = new DeviceRequestDto("Test Device", Brand.SAMSUNG, State.AVAILABLE);
        DeviceRequestDto dto2 = new DeviceRequestDto("Test Device", Brand.SAMSUNG, State.AVAILABLE);

        // Then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void equals_WithDifferentValues_ShouldNotBeEqual() {
        // Given
        DeviceRequestDto dto1 = new DeviceRequestDto("Test Device", Brand.SAMSUNG, State.AVAILABLE);
        DeviceRequestDto dto2 = new DeviceRequestDto("Different Device", Brand.APPLE, State.IN_USE);

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        // When
        String result = validDto.toString();

        // Then
        assertThat(result).contains("Test Device");
        assertThat(result).contains("SAMSUNG");
        assertThat(result).contains("AVAILABLE");
    }
}