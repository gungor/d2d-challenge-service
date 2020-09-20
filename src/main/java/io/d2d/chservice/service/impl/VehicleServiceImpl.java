package io.d2d.chservice.service.impl;

import io.d2d.chservice.exception.InvalidOperationException;
import io.d2d.chservice.model.db.Location;
import io.d2d.chservice.model.db.LocationLog;
import io.d2d.chservice.model.db.Vehicle;
import io.d2d.chservice.model.db.VehicleLog;
import io.d2d.chservice.model.rest.UpdateVehicleLocationRequest;
import io.d2d.chservice.model.rest.VehicleSearchResponse;
import io.d2d.chservice.repository.LocationLogRepository;
import io.d2d.chservice.repository.LocationRepository;
import io.d2d.chservice.repository.VehicleLogRepository;
import io.d2d.chservice.repository.VehicleRepository;
import io.d2d.chservice.service.DistanceValidatorService;
import io.d2d.chservice.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleServiceImpl implements VehicleService {

    private static Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private VehicleRepository vehicleRepository;
    private VehicleLogRepository vehicleLogRepository;
    private LocationRepository locationRepository;
    private LocationLogRepository locationLogRepository;
    private DistanceValidatorService distanceValidatorService;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              VehicleLogRepository vehicleLogRepository,
                              LocationRepository locationRepository,
                              LocationLogRepository locationLogRepository,
                              DistanceValidatorService distanceValidatorService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleLogRepository = vehicleLogRepository;
        this.locationRepository = locationRepository;
        this.locationLogRepository = locationLogRepository;
        this.distanceValidatorService = distanceValidatorService;
    }

    @Override
    public void saveVehicle(String vehicleId) {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setUUID(vehicleId);
            vehicleRepository.save(vehicle);
            VehicleLog vehicleLog = new VehicleLog();
            vehicleLog.setUuid(vehicleId);
            vehicleLogRepository.save(vehicleLog);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidOperationException("Vehicle with id: "+ vehicleId + " already exists");
        }
    }

    @Override
    public void updateVehicleLocation(String vehicleId, UpdateVehicleLocationRequest request) {
        Vehicle vehicle = vehicleRepository.getVehicleByUUID(vehicleId).orElseThrow(
                () -> { throw new InvalidOperationException("No vehicle exists for id: "+ vehicleId); }
        );

        if( !distanceValidatorService.isLocationInActiveDistance(request.getLat(), request.getLng()) ){
            if( !vehicle.getOut() ){
                vehicle.setOut(true);
                vehicleRepository.save(vehicle);
            }
            return;
        }

        if( vehicle.getOut() ){
            vehicle.setOut(false);
            vehicleRepository.save(vehicle);
        }

        // remove old locations due to performance degradation
        // hold last two locations to calculate direction
        // all location data is already stored in location_log table
        if( vehicle.getLocations().size() > 1 ){
            locationRepository.delete( vehicle.getLocations().get(1) );
        }

        Location location = new Location();
        location.setLatitude(request.getLat());
        location.setLongitude(request.getLng());
        location.setUpdateTime(request.getAt());
        location.setVehicle(vehicle);
        locationRepository.save(location);

        LocationLog locationLog = new LocationLog();
        locationLog.setLatitude(request.getLat());
        locationLog.setLongitude(request.getLng());
        locationLog.setUpdateTime(request.getAt());
        locationLog.setVehicleUUID( vehicle.getUUID() );
        locationLogRepository.save(locationLog);
    }

    @Override
    public void deleteVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.getVehicleByUUID(vehicleId).orElseThrow(
                () -> { throw new InvalidOperationException("No vehicle exists for id: "+ vehicleId); }
        );
        vehicleRepository.delete(vehicle);
    }

    @Override
    public VehicleSearchResponse searchVehicles(Double neLat, Double neLng, Double swLat, Double swLng) {
        List<Vehicle> vehicleList = vehicleRepository.getVehiclesByBoundaries(neLat, neLng, swLat, swLng);
        VehicleSearchResponse vehicleSearchResponse = new VehicleSearchResponse();
        vehicleSearchResponse.setVehicleList(vehicleList);
        return vehicleSearchResponse;
    }

    @Override
    public void truncate() {
        locationRepository.deleteAllInBatch();
        vehicleRepository.deleteAllInBatch();
        vehicleLogRepository.deleteAllInBatch();
        locationLogRepository.deleteAllInBatch();
        log.info("tables truncated");
    }

}
