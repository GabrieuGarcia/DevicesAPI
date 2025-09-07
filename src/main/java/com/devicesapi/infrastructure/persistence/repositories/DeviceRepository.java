package com.devicesapi.infrastructure.persistence.repositories;

import com.devicesapi.infrastructure.persistence.entities.DeviceEntity;
import com.devicesapi.domain.enums.Brand;
import com.devicesapi.domain.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID> {
    
    List<DeviceEntity> findByBrand(Brand brand);
    
    List<DeviceEntity> findByState(State state);
}