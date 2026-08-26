package com.omp.common.service;

import com.omp.common.entity.Location;
import com.omp.common.enums.LocationType;
import com.omp.common.repository.LocationRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class LocationService {

    @Inject
    private LocationRepository locationRepository;

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public List<Location> findByType(LocationType type) {
        return locationRepository.findByType(type);
    }

    public Location create(Location location) {
        return locationRepository.save(location);
    }
}
