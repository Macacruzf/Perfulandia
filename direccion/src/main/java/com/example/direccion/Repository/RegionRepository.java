package com.example.direccion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.direccion.model.Region;
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}
