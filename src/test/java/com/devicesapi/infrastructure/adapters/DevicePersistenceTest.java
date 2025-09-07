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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevicePersistenceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DevicePersistence devicePersistence;

    private Device testDevice;
    private DeviceEntity testDeviceEntity;
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
        testDeviceEntity = DeviceEntity.fromDomain(testDevice);
    }

    @Test
    void save_ShouldReturnSavedDevice() {
        // Given
        when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(testDeviceEntity);

        // When
        Device result = devicePersistence.save(testDevice);

        // Then
        assertNotNull(result);
        assertEquals(testDevice.getId(), result.getId());
        assertEquals(testDevice.getName(), result.getName());
        assertEquals(testDevice.getBrand(), result.getBrand());
        assertEquals(testDevice.getState(), result.getState());
        assertEquals(testDevice.getCreationTime(), result.getCreationTime());
        verify(deviceRepository).save(any(DeviceEntity.class));
    }

    @Test
    void findById_ShouldReturnDevice_WhenDeviceExists() {
        // Given
        when(deviceRepository.findById(testId)).thenReturn(Optional.of(testDeviceEntity));

        // When
        Optional<Device> result = devicePersistence.findById(testId);

        // Then
        assertTrue(result.isPresent());
        Device device = result.get();
        assertEquals(testDevice.getId(), device.getId());
        assertEquals(testDevice.getName(), device.getName());
        assertEquals(testDevice.getBrand(), device.getBrand());
        assertEquals(testDevice.getState(), device.getState());
        verify(deviceRepository).findById(testId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDeviceNotExists() {
        // Given
        when(deviceRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        Optional<Device> result = devicePersistence.findById(testId);

        // Then
        assertFalse(result.isPresent());
        verify(deviceRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnAllDevices() {
        // Given
        DeviceEntity entity2 = DeviceEntity.fromDomain(
                Device.createWithIdAndTime(
                        UUID.randomUUID(),
                        "Galaxy S24",
                        Brand.SAMSUNG,
                        State.IN_USE,
                        testTime
                )
        );
        List<DeviceEntity> entities = Arrays.asList(testDeviceEntity, entity2);
        when(deviceRepository.findAll()).thenReturn(entities);

        // When
        List<Device> result = devicePersistence.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("iPhone 15", result.get(0).getName());
        assertEquals("Galaxy S24", result.get(1).getName());
        verify(deviceRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoDevices() {
        // Given
        when(deviceRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Device> result = devicePersistence.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceRepository).findAll();
    }

    @Test
    void findByBrand_ShouldReturnDevicesWithSpecificBrand() {
        // Given
        List<DeviceEntity> appleEntities = Arrays.asList(testDeviceEntity);
        when(deviceRepository.findByBrand(Brand.APPLE)).thenReturn(appleEntities);

        // When
        List<Device> result = devicePersistence.findByBrand(Brand.APPLE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Brand.APPLE, result.get(0).getBrand());
        assertEquals("iPhone 15", result.get(0).getName());
        verify(deviceRepository).findByBrand(Brand.APPLE);
    }

    @Test
    void findByBrand_ShouldReturnEmptyList_WhenNoBrandMatches() {
        // Given
        when(deviceRepository.findByBrand(Brand.SAMSUNG)).thenReturn(Arrays.asList());

        // When
        List<Device> result = devicePersistence.findByBrand(Brand.SAMSUNG);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceRepository).findByBrand(Brand.SAMSUNG);
    }

    @Test
    void findByState_ShouldReturnDevicesWithSpecificState() {
        // Given
        List<DeviceEntity> availableEntities = Arrays.asList(testDeviceEntity);
        when(deviceRepository.findByState(State.AVAILABLE)).thenReturn(availableEntities);

        // When
        List<Device> result = devicePersistence.findByState(State.AVAILABLE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(State.AVAILABLE, result.get(0).getState());
        assertEquals("iPhone 15", result.get(0).getName());
        verify(deviceRepository).findByState(State.AVAILABLE);
    }

    @Test
    void findByState_ShouldReturnEmptyList_WhenNoStateMatches() {
        // Given
        when(deviceRepository.findByState(State.IN_USE)).thenReturn(Arrays.asList());

        // When
        List<Device> result = devicePersistence.findByState(State.IN_USE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceRepository).findByState(State.IN_USE);
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(deviceRepository).deleteById(testId);

        // When
        devicePersistence.deleteById(testId);

        // Then
        verify(deviceRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenDeviceExists() {
        // Given
        when(deviceRepository.existsById(testId)).thenReturn(true);

        // When
        boolean result = devicePersistence.existsById(testId);

        // Then
        assertTrue(result);
        verify(deviceRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenDeviceNotExists() {
        // Given
        when(deviceRepository.existsById(testId)).thenReturn(false);

        // When
        boolean result = devicePersistence.existsById(testId);

        // Then
        assertFalse(result);
        verify(deviceRepository).existsById(testId);
    }

    @Test
    void save_ShouldHandleEntityConversion() {
        // Given
        Device newDevice = Device.createNew("MacBook Pro", Brand.APPLE, State.AVAILABLE);
        DeviceEntity savedEntity = DeviceEntity.fromDomain(newDevice);
        when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(savedEntity);

        // When
        Device result = devicePersistence.save(newDevice);

        // Then
        assertNotNull(result);
        assertEquals(newDevice.getName(), result.getName());
        assertEquals(newDevice.getBrand(), result.getBrand());
        assertEquals(newDevice.getState(), result.getState());
        verify(deviceRepository).save(any(DeviceEntity.class));
    }

    @Test
    void findByBrand_ShouldHandleMultipleDevices() {
        // Given
        DeviceEntity entity1 = DeviceEntity.fromDomain(
                Device.createWithIdAndTime(
                        UUID.randomUUID(),
                        "iPhone 15",
                        Brand.APPLE,
                        State.AVAILABLE,
                        testTime
                )
        );
        DeviceEntity entity2 = DeviceEntity.fromDomain(
                Device.createWithIdAndTime(
                        UUID.randomUUID(),
                        "MacBook Pro",
                        Brand.APPLE,
                        State.IN_USE,
                        testTime
                )
        );
        List<DeviceEntity> appleEntities = Arrays.asList(entity1, entity2);
        when(deviceRepository.findByBrand(Brand.APPLE)).thenReturn(appleEntities);

        // When
        List<Device> result = devicePersistence.findByBrand(Brand.APPLE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(device -> device.getBrand() == Brand.APPLE));
        assertEquals("iPhone 15", result.get(0).getName());
        assertEquals("MacBook Pro", result.get(1).getName());
        verify(deviceRepository).findByBrand(Brand.APPLE);
    }

    @Test
    void findByState_ShouldHandleMultipleDevices() {
        // Given
        DeviceEntity entity1 = DeviceEntity.fromDomain(
                Device.createWithIdAndTime(
                        UUID.randomUUID(),
                        "iPhone 15",
                        Brand.APPLE,
                        State.AVAILABLE,
                        testTime
                )
        );
        DeviceEntity entity2 = DeviceEntity.fromDomain(
                Device.createWithIdAndTime(
                        UUID.randomUUID(),
                        "Galaxy S24",
                        Brand.SAMSUNG,
                        State.AVAILABLE,
                        testTime
                )
        );
        List<DeviceEntity> availableEntities = Arrays.asList(entity1, entity2);
        when(deviceRepository.findByState(State.AVAILABLE)).thenReturn(availableEntities);

        // When
        List<Device> result = devicePersistence.findByState(State.AVAILABLE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(device -> device.getState() == State.AVAILABLE));
        assertEquals("iPhone 15", result.get(0).getName());
        assertEquals("Galaxy S24", result.get(1).getName());
        verify(deviceRepository).findByState(State.AVAILABLE);
    }
}