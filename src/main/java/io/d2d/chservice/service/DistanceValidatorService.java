package io.d2d.chservice.service;

public interface DistanceValidatorService {

    boolean isLocationInActiveDistance(Double latitude, Double longitude);

}
