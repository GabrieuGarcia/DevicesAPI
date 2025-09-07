package com.devicesapi.infrastructure.adapters;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;

import com.devicesapi.infrastructure.persistence.entities.DeviceEntity;
import com.devicesapi.infrastructure.persistence.repositories.DeviceRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevicePersistenceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DevicePersistence devicePersistence;

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
    void save_ShouldSaveDeviceAndReturnSavedDevice() {
        // Given
        DeviceEntity savedEntity = new DeviceEntity(testId, testName, testBrand, testState, testTime);
        when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(savedEntity);

        // When
        Device result = devicePersistence.save(testDevice);

        // Then
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getName()).isEqualTo(testName);
        assertThat(result.getBrand()).isEqualTo(testBrand);
        assertThat(result.getState()).isEqualTo(testState);
        assertThat(result.getCreationTime()).isEqualTo(testTime);
        
        verify(deviceRepository).save(any(DeviceEntity.class));
    }

    @Test
    void save_WithNewDevice_ShouldGenerateIdAndSave() {
        // Given
        Device newDevice = Device.createNew(testName, testBrand, testState);
        DeviceEntity savedEntity = new DeviceEntity(testId, testName, testBrand, testState, newDevice.getCreationTime());
        when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(savedEntity);

        // When
        Device result = devicePersistence.save(newDevice);

        // Then
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getName()).isEqualTo(testName);
        assertThat(result.getBrand()).isEqualTo(testBrand);
        assertThat(result.getState()).isEqualTo(testState);
        
        verify(deviceRepository).save(any(DeviceEntity.class));
    }

    @Test
    void findById_WhenDeviceExists_ShouldReturnOptionalWithDevice() {
        // Given
        when(deviceRepository.findById(testId)).thenReturn(Optional.of(testEntity));

        // When
        Optional<Device> result = devicePersistence.findById(testId);

        // Then
        assertThat(result).isPresent();
        Device device = result.get();
        assertThat(device.getId()).isEqualTo(testId);
        assertThat(device.getName()).isEqualTo(testName);
        assertThat(device.getBrand()).isEqualTo(testBrand);
        assertThat(device.getState()).isEqualTo(testState);
        assertThat(device.getCreationTime()).isEqualTo(testTime);
        
        verify(deviceRepository).findById(testId);
    }

    @Test
    void findById_WhenDeviceNotExists_ShouldReturnEmptyOptional() {
        // Given
        when(deviceRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        Optional<Device> result = devicePersistence.findById(testId);

        // Then
        assertThat(result).isEmpty();
        verify(deviceRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnAllDevices() {
        // Given
        DeviceEntity entity1 = new DeviceEntity(UUID.randomUUID(), "Device 1", Brand.APPLE, State.AVAILABLE, LocalDateTime.now());
        DeviceEntity entity2 = new DeviceEntity(UUID.randomUUID(), "Device 2", Brand.GOOGLE, State.IN_USE, LocalDateTime.now());
        List<DeviceEntity> entities = Arrays.asList(entity1, entity2);
        
        when(deviceRepository.findAll()).thenReturn(entities);

        // When
        List<Device> result = devicePersistence.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Device 1");
        assertThat(result.get(0).getBrand()).isEqualTo(Brand.APPLE);
        assertThat(result.get(1).getName()).isEqualTo("Device 2");
        assertThat(result.get(1).getBrand()).isEqualTo(Brand.GOOGLE);
        
        verify(deviceRepository).findAll();
    }

    @Test
    void findAll_WhenNoDevices_ShouldReturnEmptyList() {
        // Given
        when(deviceRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Device> result = devicePersistence.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(deviceRepository).findAll();
    }

    @Test
    void findByState_ShouldReturnDevicesWithSpecificState() {
        // Given
        DeviceEntity entity1 = new DeviceEntity(UUID.randomUUID(), "Device 1", Brand.SAMSUNG, State.AVAILABLE, LocalDateTime.now());
        DeviceEntity entity2 = new DeviceEntity(UUID.randomUUID(), "Device 2", Brand.APPLE, State.AVAILABLE, LocalDateTime.now());
        List<DeviceEntity> entities = Arrays.asList(entity1, entity2);
        
        when(deviceRepository.findByState(State.AVAILABLE)).thenReturn(entities);

        // When
        List<Device> result = devicePersistence.findByState(State.AVAILABLE);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getState()).isEqualTo(State.AVAILABLE);
        assertThat(result.get(1).getState()).isEqualTo(State.AVAILABLE);
        
        verify(deviceRepository).findByState(State.AVAILABLE);
    }

    @Test
    void findByBrand_ShouldReturnDevicesWithSpecificBrand() {
        // Given
        DeviceEntity entity1 = new DeviceEntity(UUID.randomUUID(), "Galaxy S21", Brand.SAMSUNG, State.AVAILABLE, LocalDateTime.now());
        DeviceEntity entity2 = new DeviceEntity(UUID.randomUUID(), "Galaxy Note", Brand.SAMSUNG, State.IN_USE, LocalDateTime.now());
        List<DeviceEntity> entities = Arrays.asList(entity1, entity2);
        
        when(deviceRepository.findByBrand(Brand.SAMSUNG)).thenReturn(entities);

        // When
        List<Device> result = devicePersistence.findByBrand(Brand.SAMSUNG);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBrand()).isEqualTo(Brand.SAMSUNG);
        assertThat(result.get(1).getBrand()).isEqualTo(Brand.SAMSUNG);
        
        verify(deviceRepository).findByBrand(Brand.SAMSUNG);
    }



    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // When
        devicePersistence.deleteById(testId);

        // Then
        verify(deviceRepository).deleteById(testId);
    }

    @Test
    void existsById_WhenDeviceExists_ShouldReturnTrue() {
        // Given
        when(deviceRepository.existsById(testId)).thenReturn(true);

        // When
        boolean result = devicePersistence.existsById(testId);

        // Then
        assertThat(result).isTrue();
        verify(deviceRepository).existsById(testId);
    }

    @Test
    void existsById_WhenDeviceNotExists_ShouldReturnFalse() {
        // Given
        when(deviceRepository.existsById(testId)).thenReturn(false);

        // When
        boolean result = devicePersistence.existsById(testId);

        // Then
        assertThat(result).isFalse();
        verify(deviceRepository).existsById(testId);
    }

    @Test
    void testAllBrandsAndStates_ShouldWorkCorrectly() {
        // Test with all combinations of brands and states
        for (Brand brand : Brand.values()) {
            for (State state : State.values()) {
                DeviceEntity entity = new DeviceEntity(testId, testName, brand, state, testTime);
                when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(entity);
                
                Device device = Device.createWithIdAndTime(testId, testName, brand, state, testTime);
                Device result = devicePersistence.save(device);
                
                assertThat(result.getBrand()).isEqualTo(brand);
                assertThat(result.getState()).isEqualTo(state);
            }
        }
    }
}