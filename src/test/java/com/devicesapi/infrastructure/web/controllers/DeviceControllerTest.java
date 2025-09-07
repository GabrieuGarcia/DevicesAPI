package com.devicesapi.infrastructure.web.controllers;

import com.devicesapi.application.dto.DeviceRequestDto;
import com.devicesapi.application.dto.DeviceResponseDto;
import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import com.devicesapi.domain.exception.DeviceNotFoundException;
import com.devicesapi.domain.ports.DeviceServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceServicePort deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Device testDevice;
    private DeviceRequestDto testRequestDto;
    private DeviceResponseDto testResponseDto;
    private UUID testId;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testTime = LocalDateTime.now();
        testDevice = Device.createWithIdAndTime(testId, "Test Device", Brand.SAMSUNG, State.AVAILABLE, testTime);
        testRequestDto = new DeviceRequestDto("Test Device", Brand.SAMSUNG, State.AVAILABLE);
        testResponseDto = DeviceResponseDto.fromDomain(testDevice);
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
                .andExpect(jsonPath("$.name").value("Test Device"))
                .andExpect(jsonPath("$.brand").value("SAMSUNG"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        verify(deviceService).createDevice(any(Device.class));
    }

    @Test
    void getDeviceById_WhenDeviceExists_ShouldReturnDevice() throws Exception {
        // Given
        when(deviceService.getDeviceById(testId)).thenReturn(Optional.of(testDevice));

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("Test Device"))
                .andExpect(jsonPath("$.brand").value("SAMSUNG"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        verify(deviceService).getDeviceById(testId);
    }

    @Test
    void getDeviceById_WhenDeviceNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(deviceService.getDeviceById(testId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", testId))
                .andExpect(status().isNotFound());

        verify(deviceService).getDeviceById(testId);
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() throws Exception {
        // Given
        Device device2 = Device.createWithIdAndTime(UUID.randomUUID(), "Device 2", Brand.APPLE, State.IN_USE, testTime);
        List<Device> devices = Arrays.asList(testDevice, device2);
        when(deviceService.getAllDevices()).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Device"))
                .andExpect(jsonPath("$[1].name").value("Device 2"));

        verify(deviceService).getAllDevices();
    }

    @Test
    void getDevicesByState_WithValidState_ShouldReturnDevices() throws Exception {
        // Given
        List<Device> availableDevices = Arrays.asList(testDevice);
        when(deviceService.getDevicesByState(State.AVAILABLE)).thenReturn(availableDevices);

        // When & Then
        mockMvc.perform(get("/api/devices/state/{state}", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].state").value("AVAILABLE"));

        verify(deviceService).getDevicesByState(State.AVAILABLE);
    }

    @Test
    void getDevicesByState_WithInvalidState_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/devices/state/{state}", "invalid_state"))
                .andExpect(status().isBadRequest());

        verify(deviceService, never()).getDevicesByState(any());
    }

    @Test
    void getDevicesByBrand_WithValidBrand_ShouldReturnDevices() throws Exception {
        // Given
        List<Device> samsungDevices = Arrays.asList(testDevice);
        when(deviceService.getDevicesByBrand(Brand.SAMSUNG)).thenReturn(samsungDevices);

        // When & Then
        mockMvc.perform(get("/api/devices/brand/{brand}", "samsung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("SAMSUNG"));

        verify(deviceService).getDevicesByBrand(Brand.SAMSUNG);
    }

    @Test
    void getDevicesByBrand_WithInvalidBrand_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/devices/brand/{brand}", "invalid_brand"))
                .andExpect(status().isBadRequest());

        verify(deviceService, never()).getDevicesByBrand(any());
    }

    @Test
    void updateDevice_ShouldReturnUpdatedDevice() throws Exception {
        // Given
        Device updatedDevice = Device.createWithIdAndTime(testId, "Updated Device", Brand.APPLE, State.IN_USE, testTime);
        DeviceRequestDto updateRequestDto = new DeviceRequestDto("Updated Device", Brand.APPLE, State.IN_USE);
        when(deviceService.updateDevice(eq(testId), any(Device.class))).thenReturn(updatedDevice);

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Device"))
                .andExpect(jsonPath("$.brand").value("APPLE"))
                .andExpect(jsonPath("$.state").value("IN_USE"));

        verify(deviceService).updateDevice(eq(testId), any(Device.class));
    }

    @Test
    void updateDevice_WhenDeviceNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        DeviceRequestDto updateRequestDto = new DeviceRequestDto("Updated Device", Brand.APPLE, State.IN_USE);
        when(deviceService.updateDevice(eq(testId), any(Device.class)))
                .thenThrow(new DeviceNotFoundException("Device with id '" + testId + "' not found"));

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound());

        verify(deviceService).updateDevice(eq(testId), any(Device.class));
    }

    @Test
    void patchDevice_ShouldReturnOk() throws Exception {
        // Given
        DeviceRequestDto patchRequestDto = new DeviceRequestDto("Patched Device", Brand.GOOGLE, State.INACTIVE);
        doNothing().when(deviceService).patchDevice(eq(testId), any(Device.class));

        // When & Then
        mockMvc.perform(patch("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequestDto)))
                .andExpect(status().isOk());

        verify(deviceService).patchDevice(eq(testId), any(Device.class));
    }

    @Test
    void patchDevice_WhenDeviceNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        DeviceRequestDto patchRequestDto = new DeviceRequestDto("Patched Device", Brand.GOOGLE, State.INACTIVE);
        doThrow(new DeviceNotFoundException("Device with id '" + testId + "' not found"))
                .when(deviceService).patchDevice(eq(testId), any(Device.class));

        // When & Then
        mockMvc.perform(patch("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequestDto)))
                .andExpect(status().isNotFound());

        verify(deviceService).patchDevice(eq(testId), any(Device.class));
    }

    @Test
    void deleteDevice_ShouldReturnOk() throws Exception {
        // Given
        doNothing().when(deviceService).deleteDevice(testId);

        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", testId))
                .andExpect(status().isOk());

        verify(deviceService).deleteDevice(testId);
    }

    @Test
    void deleteDevice_WhenDeviceNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new DeviceNotFoundException("Device with id '" + testId + "' not found"))
                .when(deviceService).deleteDevice(testId);

        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", testId))
                .andExpect(status().isNotFound());

        verify(deviceService).deleteDevice(testId);
    }
}