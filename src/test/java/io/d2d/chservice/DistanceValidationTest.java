package io.d2d.chservice;

import io.d2d.chservice.service.DistanceValidatorService;
import io.d2d.chservice.service.impl.DistanceValidatorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {DistanceValidatorServiceImpl.class})
@ActiveProfiles({"dev"})
public class DistanceValidationTest {

    private DistanceValidatorService distanceValidatorService;

    @Autowired
    public DistanceValidationTest(DistanceValidatorService distanceValidatorService) {
        this.distanceValidatorService = distanceValidatorService;
    }


    @Test
    public void shouldValidateDistances(){
        Assertions.assertFalse( distanceValidatorService.isLocationInActiveDistance(52.57, 13.404) );
        Assertions.assertFalse( distanceValidatorService.isLocationInActiveDistance(52.53, 13.34) );
        Assertions.assertFalse( distanceValidatorService.isLocationInActiveDistance(52.53, 13.35) );
        Assertions.assertFalse( distanceValidatorService.isLocationInActiveDistance(52.53, 13.351) );

        Assertions.assertTrue( distanceValidatorService.isLocationInActiveDistance(52.53, 13.352) );
        Assertions.assertTrue( distanceValidatorService.isLocationInActiveDistance(52.56, 13.404) );
        Assertions.assertTrue( distanceValidatorService.isLocationInActiveDistance(52.50, 13.404) );
        Assertions.assertTrue( distanceValidatorService.isLocationInActiveDistance(52.53, 13.39) );
    }
}
