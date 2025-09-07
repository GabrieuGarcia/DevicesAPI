package com.devicesapi.infrastructure.persistence.repositories;

import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.infrastructure.persistence.entities.DeviceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DeviceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeviceRepository deviceRepository;

    private DeviceEntity testDevice1;
    private DeviceEntity testDevice2;

    @BeforeEach
    void setUp() {
        testDevice1 = DeviceEntity.createWithTime(
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE,
                LocalDateTime.now()
        );

        testDevice2 = DeviceEntity.createWithTime(
                "Galaxy S24",
                Brand.SAMSUNG,
                State.IN_USE,
                LocalDateTime.now()
        );

        testDevice1 = entityManager.persistAndFlush(testDevice1);
        testDevice2 = entityManager.persistAndFlush(testDevice2);
    }

    @Test
    void findByBrand_ShouldReturnDevicesWithSpecificBrand() {
        // When
        List<DeviceEntity> appleDevices = deviceRepository.findByBrand(Brand.APPLE);
        List<DeviceEntity> samsungDevices = deviceRepository.findByBrand(Brand.SAMSUNG);

        // Then
        assertEquals(1, appleDevices.size());
        assertEquals("iPhone 15", appleDevices.get(0).getName());
        
        assertEquals(1, samsungDevices.size());
        assertEquals("Galaxy S24", samsungDevices.get(0).getName());
    }

    @Test
    void findByState_ShouldReturnDevicesWithSpecificState() {
        // When
        List<DeviceEntity> availableDevices = deviceRepository.findByState(State.AVAILABLE);
        List<DeviceEntity> inUseDevices = deviceRepository.findByState(State.IN_USE);

        // Then
        assertEquals(1, availableDevices.size());
        assertEquals("iPhone 15", availableDevices.get(0).getName());
        
        assertEquals(1, inUseDevices.size());
        assertEquals("Galaxy S24", inUseDevices.get(0).getName());
    }



    @Test
    void save_ShouldPersistDevice() {
        // Given
        DeviceEntity newDevice = DeviceEntity.createWithTime(
                "Pixel 8",
                Brand.GOOGLE,
                State.INACTIVE,
                LocalDateTime.now()
        );

        // When
        DeviceEntity savedDevice = deviceRepository.save(newDevice);

        // Then
        assertNotNull(savedDevice.getId());
        assertEquals("Pixel 8", savedDevice.getName());
        assertEquals(Brand.GOOGLE, savedDevice.getBrand());
        assertEquals(State.INACTIVE, savedDevice.getState());
        
        // Verify it's actually persisted
        assertTrue(deviceRepository.existsById(savedDevice.getId()));
    }

    @Test
    void deleteById_ShouldRemoveDevice() {
        // Given
        UUID deviceId = testDevice1.getId();
        assertTrue(deviceRepository.existsById(deviceId));

        // When
        deviceRepository.deleteById(deviceId);

        // Then
        assertFalse(deviceRepository.existsById(deviceId));
    }
}