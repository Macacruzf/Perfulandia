package com.example.direccion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.direccion.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
