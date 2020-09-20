package io.d2d.chservice.controller;

import io.d2d.chservice.model.rest.RegisterVehicleRequest;
import io.d2d.chservice.model.rest.UpdateVehicleLocationRequest;
import io.d2d.chservice.model.rest.VehicleSearchResponse;
import io.d2d.chservice.service.VehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Api
@RestController
public class Controller {

    private static Logger log = LoggerFactory.getLogger(Controller.class);

    private VehicleService vehicleService;

    public Controller(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @ApiOperation(value = "/vehicles")
    @PostMapping(value = "/vehicles")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void registerVehicle(@Valid @RequestBody RegisterVehicleRequest request){
        vehicleService.saveVehicle(request.getId());
    }

    @ApiOperation(value = "/vehicles/{id}/locations")
    @PostMapping(value = "/vehicles/{id}/locations")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateVehicleLocation(@Valid @RequestBody UpdateVehicleLocationRequest request,
                                      @PathVariable @NotEmpty String id){
        vehicleService.updateVehicleLocation(id,request);
    }

    @ApiOperation(value = "/vehicles/{id}")
    @DeleteMapping(value = "/vehicles/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable @NotEmpty String id){
        vehicleService.deleteVehicle(id);
    }

    @ApiOperation(value = "/vehicles/{neLat}/{neLng}/{swLat}/{swLng}")
    @GetMapping(value = "/vehicles/{neLat}/{neLng}/{swLat}/{swLng}")
    public VehicleSearchResponse searchVehicles(@PathVariable @NotEmpty Double neLat,
                                                @PathVariable @NotEmpty Double neLng,
                                                @PathVariable @NotEmpty Double swLat,
                                                @PathVariable @NotEmpty Double swLng){
        long start = System.currentTimeMillis();
        VehicleSearchResponse response = vehicleService.searchVehicles(neLat, neLng, swLat, swLng);
        log.info( "search took : "+ (System.currentTimeMillis()-start) + " ms" );
        return response;
    }

    @GetMapping(value = "/reset")
    public void truncateTables(){
        vehicleService.truncate();
    }



}
