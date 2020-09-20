package io.d2d.chservice.service;

import io.d2d.chservice.model.rest.UpdateVehicleLocationRequest;
import io.d2d.chservice.model.rest.VehicleSearchResponse;

public interface VehicleService {

    void saveVehicle(String vehicleId);

    void updateVehicleLocation(String vehicleId, UpdateVehicleLocationRequest request);

    void deleteVehicle(String vehicleId);

    VehicleSearchResponse searchVehicles(Double neLat, Double neLng, Double swLat, Double swLng);

    void truncate();
}
