package io.d2d.chservice.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.d2d.chservice.model.db.Vehicle;
import lombok.Data;

import java.util.List;

@Data
public class VehicleSearchResponse {

    @JsonProperty("vehicleList")
    private List<Vehicle> vehicleList;

}
