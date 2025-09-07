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
import org.springframework.web.server.ResponseStatusException;

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
    private DeviceServicePort deviceServicePort;

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
        testDevice = Device.createWithIdAndTime(
                testId,
                "iPhone 15",
                Brand.APPLE,
                State.AVAILABLE,
                testTime
        );
        testRequestDto = new DeviceRequestDto("iPhone 15", Brand.APPLE, State.AVAILABLE);
        testResponseDto = DeviceResponseDto.fromDomain(testDevice);
    }

    @Test
    void createDevice_ShouldReturnCreated_WhenValidRequest() throws Exception {
        // Given
        doNothing().when(deviceServicePort).createDevice(any(Device.class));

        // When & Then
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isCreated());

        verify(deviceServicePort).createDevice(any(Device.class));
    }

    @Test
    void createDevice_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        DeviceRequestDto invalidDto = new DeviceRequestDto(null, null, null);

        // When & Then
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(deviceServicePort, never()).createDevice(any(Device.class));
    }

    @Test
    void getDeviceById_ShouldReturnDevice_WhenDeviceExists() throws Exception {
        // Given
        when(deviceServicePort.getDeviceById(testId)).thenReturn(Optional.of(testDevice));

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("APPLE"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        verify(deviceServicePort).getDeviceById(testId);
    }

    @Test
    void getDeviceById_ShouldReturnNotFound_WhenDeviceNotExists() throws Exception {
        // Given
        when(deviceServicePort.getDeviceById(testId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", testId))
                .andExpect(status().isNotFound());

        verify(deviceServicePort).getDeviceById(testId);
    }

    @Test
    void patchDevice_ShouldReturnOk_WhenValidRequest() throws Exception {
        // Given
        doNothing().when(deviceServicePort).patchDevice(eq(testId), any(Device.class));

        // When & Then
        mockMvc.perform(patch("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isOk());

        verify(deviceServicePort).patchDevice(eq(testId), any(Device.class));
    }

    @Test
    void patchDevice_ShouldReturnNotFound_WhenDeviceNotExists() throws Exception {
        // Given
        doThrow(new DeviceNotFoundException("Device with id '" + testId + "' not found"))
                .when(deviceServicePort).patchDevice(eq(testId), any(Device.class));

        // When & Then
        mockMvc.perform(patch("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isNotFound());

        verify(deviceServicePort).patchDevice(eq(testId), any(Device.class));
    }

    @Test
    void getDeviceByState_ShouldReturnDevices_WhenValidState() throws Exception {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceServicePort.getDevicesByState(State.AVAILABLE)).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices/state/{state}", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].state").value("AVAILABLE"));

        verify(deviceServicePort).getDevicesByState(State.AVAILABLE);
    }

    @Test
    void getDeviceByState_ShouldReturnBadRequest_WhenInvalidState() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/devices/state/{state}", "invalid_state"))
                .andExpect(status().isBadRequest());

        verify(deviceServicePort, never()).getDevicesByState(any(State.class));
    }

    @Test
    void getDeviceByBrand_ShouldReturnDevices_WhenValidBrand() throws Exception {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceServicePort.getDevicesByBrand(Brand.APPLE)).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices/brand/{brand}", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].brand").value("APPLE"));

        verify(deviceServicePort).getDevicesByBrand(Brand.APPLE);
    }

    @Test
    void getDeviceByBrand_ShouldReturnBadRequest_WhenInvalidBrand() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/devices/brand/{brand}", "invalid_brand"))
                .andExpect(status().isBadRequest());

        verify(deviceServicePort, never()).getDevicesByBrand(any(Brand.class));
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() throws Exception {
        // Given
        Device device2 = Device.createWithIdAndTime(
                UUID.randomUUID(),
                "Galaxy S24",
                Brand.SAMSUNG,
                State.IN_USE,
                testTime
        );
        List<Device> devices = Arrays.asList(testDevice, device2);
        when(deviceServicePort.getAllDevices()).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$[1].name").value("Galaxy S24"));

        verify(deviceServicePort).getAllDevices();
    }

    @Test
    void getAllDevices_ShouldReturnEmptyList_WhenNoDevices() throws Exception {
        // Given
        when(deviceServicePort.getAllDevices()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(deviceServicePort).getAllDevices();
    }

    @Test
    void updateDevice_ShouldReturnOk_WhenValidRequest() throws Exception {
        // Given
        doNothing().when(deviceServicePort).updateDevice(eq(testId), any(Device.class));

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isOk());

        verify(deviceServicePort).updateDevice(eq(testId), any(Device.class));
    }

    @Test
    void updateDevice_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        DeviceRequestDto invalidDto = new DeviceRequestDto(null, null, null);

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(deviceServicePort, never()).updateDevice(eq(testId), any(Device.class));
    }

    @Test
    void updateDevice_ShouldReturnNotFound_WhenDeviceNotExists() throws Exception {
        // Given
        doThrow(new DeviceNotFoundException("Device with id '" + testId + "' not found"))
                .when(deviceServicePort).updateDevice(eq(testId), any(Device.class));

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isNotFound());

        verify(deviceServicePort).updateDevice(eq(testId), any(Device.class));
    }

    @Test
    void deleteDevice_ShouldReturnOk_WhenDeviceExists() throws Exception {
        // Given
        doNothing().when(deviceServicePort).deleteDevice(testId);

        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", testId))
                .andExpect(status().isOk());

        verify(deviceServicePort).deleteDevice(testId);
    }

    @Test
    void deleteDevice_ShouldReturnNotFound_WhenDeviceNotExists() throws Exception {
        // Given
        doThrow(new DeviceNotFoundException("Device with id '" + testId + "' not found"))
                .when(deviceServicePort).deleteDevice(testId);

        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", testId))
                .andExpect(status().isNotFound());

        verify(deviceServicePort).deleteDevice(testId);
    }

    @Test
    void deleteDevice_ShouldReturnNotFound_WhenDeviceInUse() throws Exception {
        // Given
        doThrow(new DeviceNotFoundException("Device with id '" + testId + "' is still in use and cannot be deleted"))
                .when(deviceServicePort).deleteDevice(testId);

        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", testId))
                .andExpect(status().isNotFound());

        verify(deviceServicePort).deleteDevice(testId);
    }
}