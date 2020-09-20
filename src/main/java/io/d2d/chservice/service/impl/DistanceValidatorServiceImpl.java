package io.d2d.chservice.service.impl;

import io.d2d.chservice.service.DistanceValidatorService;
import org.apache.lucene.util.SloppyMath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DistanceValidatorServiceImpl implements DistanceValidatorService {

    @Value("${d2d.center.latitude}")
    private Double centerLatitude;

    @Value("${d2d.center.longitude}")
    private Double centerLongitude;

    @Value("${d2d.center.activeDistance}")
    private Double activeDistance;

    @Override
    public boolean isLocationInActiveDistance(Double latitude, Double longitude) {
        double vehicleDistanceToCenter = SloppyMath.haversinMeters(
                centerLatitude, centerLongitude, latitude, longitude);
        return vehicleDistanceToCenter < activeDistance;
    }
}
