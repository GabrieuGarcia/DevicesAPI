package com.devicesapi.infrastructure.web.controllers;

import com.devicesapi.application.dto.DeviceRequestDto;
import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.ports.DeviceServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
@ActiveProfiles("test")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceServicePort deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Device testDevice;
    private DeviceRequestDto testRequestDto;
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
        
        testRequestDto = new DeviceRequestDto("iPhone 15", Brand.APPLE, State.AVAILABLE);
    }

    @Test
    void createDevice_ShouldReturnCreatedDevice() throws Exception {
        // Given
        when(deviceService.createDevice(any(Device.class))).thenReturn(testDevice);

        // When & Then
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("APPLE"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }

    @Test
    void createDevice_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Given
        DeviceRequestDto invalidRequest = new DeviceRequestDto("", Brand.APPLE, State.AVAILABLE);

        // When & Then
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDeviceById_ShouldReturnDevice_WhenDeviceExists() throws Exception {
        // Given
        when(deviceService.getDeviceById(testId)).thenReturn(Optional.of(testDevice));

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("APPLE"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }

    @Test
    void getDeviceById_ShouldReturnNotFound_WhenDeviceDoesNotExist() throws Exception {
        // Given
        when(deviceService.getDeviceById(testId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", testId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() throws Exception {
        // Given
        when(deviceService.getAllDevices()).thenReturn(Arrays.asList(testDevice));

        // When & Then
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].name").value("iPhone 15"));
    }

    @Test
    void getAllDevices_ShouldFilterByBrand() throws Exception {
        // Given
        when(deviceService.getDevicesByBrand(Brand.APPLE)).thenReturn(Arrays.asList(testDevice));

        // When & Then
        mockMvc.perform(get("/api/devices")
                        .param("brand", "APPLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].brand").value("APPLE"));
    }

    @Test
    void updateDevice_ShouldReturnUpdatedDevice() throws Exception {
        // Given
        Device updatedDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15 Pro",
                Brand.APPLE,
                State.IN_USE,
                testDevice.getCreationTime()
        );
        
        DeviceRequestDto updateRequest = new DeviceRequestDto("iPhone 15 Pro", Brand.APPLE, State.IN_USE);
        
        when(deviceService.getDeviceById(testId)).thenReturn(Optional.of(testDevice));
        when(deviceService.updateDevice(eq(testId), any(Device.class))).thenReturn(updatedDevice);

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.state").value("IN_USE"));
    }

    @Test
    void deleteDevice_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", testId))
                .andExpect(status().isNoContent());
    }
}