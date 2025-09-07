package com.devicesapi.infrastructure.persistence.entities;

import com.devicesapi.domain.entities.Device;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Brand brand;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;
    
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;
    
    // Static factory methods for creating entities
    public static DeviceEntity createWithTime(String name, Brand brand, State state, LocalDateTime creationTime) {
        return new DeviceEntity(null, name, brand, state, creationTime);
    }
    
    public static DeviceEntity fromDomain(Device device) {
        return new DeviceEntity(
                device.getId(),
                device.getName(),
                device.getBrand(),
                device.getState(),
                device.getCreationTime()
        );
    }
    
    public Device toDomain() {
        return Device.createWithIdAndTime(this.id, this.name, this.brand, this.state, this.creationTime);
    }
}