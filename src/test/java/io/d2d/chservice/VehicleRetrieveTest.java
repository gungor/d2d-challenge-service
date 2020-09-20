package io.d2d.chservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.d2d.chservice.config.Config;
import io.d2d.chservice.controller.Controller;
import io.d2d.chservice.controller.ExceptionHandler;
import io.d2d.chservice.model.db.Vehicle;
import io.d2d.chservice.model.rest.VehicleSearchResponse;
import io.d2d.chservice.repository.LocationLogRepository;
import io.d2d.chservice.repository.LocationRepository;
import io.d2d.chservice.repository.VehicleLogRepository;
import io.d2d.chservice.repository.VehicleRepository;
import io.d2d.chservice.service.VehicleService;
import io.d2d.chservice.service.impl.DistanceValidatorServiceImpl;
import io.d2d.chservice.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration( classes = { Config.class} )
@ImportAutoConfiguration({
        ServletWebServerFactoryAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        RestTemplateAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MockMvcAutoConfiguration.class
})
@AutoConfigureMockMvc
@Import(value = {Controller.class, ExceptionHandler.class,
                VehicleServiceImpl.class, DistanceValidatorServiceImpl.class})
@EnableJpaRepositories(basePackages = {"io.d2d.chservice.repository"})
@EntityScan(basePackages="io.d2d.chservice.model")
@EnableWebMvc
@ActiveProfiles({"dev"})
public class VehicleRetrieveTest {

    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private VehicleRepository vehicleRepository;
    private VehicleLogRepository vehicleLogRepository;
    private LocationRepository locationRepository;
    private LocationLogRepository locationLogRepository;
    private VehicleService vehicleService;

    @Autowired
    public VehicleRetrieveTest(MockMvc mvc,
                               VehicleRepository vehicleRepository,
                               VehicleLogRepository vehicleLogRepository,
                               LocationRepository locationRepository,
                               LocationLogRepository locationLogRepository,
                               VehicleService vehicleService) {
        this.mvc = mvc;
        this.vehicleRepository = vehicleRepository;
        this.vehicleLogRepository = vehicleLogRepository;
        this.locationRepository = locationRepository;
        this.locationLogRepository = locationLogRepository;
        this.vehicleService = vehicleService;
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidParameterReceived() throws Exception {
        MvcResult result = mvc.perform(get("/vehicles/52.54/13.409/52/13")).andReturn();
        VehicleSearchResponse vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                VehicleSearchResponse.class);


    }

    @Test
    public void shouldReturnVehiclesInsideBoundaries() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content( "{ \"id\": \""+uuid+"\" }"));

        for(int i=0 ; i< 3; i++){
            mvc.perform(post("/vehicles/"+uuid+"/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"lat\": 52.53, \"lng\": 13.40, \"at\": \"2020-09-16T12:00:00Z\" }"));
        }

        uuid = "60b0207a-2611-479a-9d1a-e1debdfdc6ed";
        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content( "{ \"id\": \""+uuid+"\" }"));

        for(int i=0 ; i< 2; i++){
            mvc.perform(post("/vehicles/"+uuid+"/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"lat\": 52.53, \"lng\": "+(13.40+i/1000D)+", \"at\": \"2020-09-16T12:00:00Z\" }"));
        }

        uuid = "dcb209ed-d557-4b6b-bcb8-e3339533e4de";
        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content( "{ \"id\": \""+uuid+"\" }"));

        for(int i=0 ; i< 2; i++){
            mvc.perform(post("/vehicles/"+uuid+"/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"lat\": 51.99, \"lng\": 13.40 , \"at\": \"2020-09-16T12:00:00Z\" }"));
        }

        MvcResult result = mvc.perform(get("/vehicles/52.54/13.409/52/13"))
                .andDo(print())
                .andReturn();
        VehicleSearchResponse vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                 VehicleSearchResponse.class);

        Assertions.assertEquals( 3, vehicleRepository.count() );
        Assertions.assertEquals(2,vehicleResponse.getVehicleList().size());
    }

    @Test
    public void shouldChangeVehicleStatusFromOutToIn() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content( "{ \"id\": \""+uuid+"\" }"));

        mvc.perform(post("/vehicles/"+uuid+"/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"lat\": 52.53, \"lng\": 13.40, \"at\": \"2020-09-16T12:00:00Z\" }"));

        MvcResult result = mvc.perform(get("/vehicles/53/14/52/13")).andReturn();
        VehicleSearchResponse vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                VehicleSearchResponse.class);
        Assertions.assertEquals(1,vehicleResponse.getVehicleList().size());
        Vehicle vehicle = vehicleRepository.findAll().get(0);
        Assertions.assertFalse( vehicle.getOut() );

        mvc.perform(post("/vehicles/"+uuid+"/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"lat\": 52.53, \"lng\": 13.60, \"at\": \"2020-09-16T12:00:00Z\" }"));

        result = mvc.perform(get("/vehicles/53/14/52/13")).andReturn();
        vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                VehicleSearchResponse.class);
        Assertions.assertEquals(0,vehicleResponse.getVehicleList().size());
        vehicle = vehicleRepository.findAll().get(0);
        Assertions.assertTrue( vehicle.getOut() );

        mvc.perform(post("/vehicles/"+uuid+"/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"lat\": 52.53, \"lng\": 13.40, \"at\": \"2020-09-16T12:00:00Z\" }"));

        result = mvc.perform(get("/vehicles/53/14/52/13")).andReturn();
        vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                VehicleSearchResponse.class);
        Assertions.assertEquals(1,vehicleResponse.getVehicleList().size());
        vehicle = vehicleRepository.findAll().get(0);
        Assertions.assertFalse( vehicle.getOut() );
    }

    @Test
    public void shouldNotRetrieveWhenLastLocationOutOfBoundaries() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content( "{ \"id\": \""+uuid+"\" }"));

        mvc.perform(post("/vehicles/"+uuid+"/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"lat\": 52.55, \"lng\": 13.40, \"at\": \"2020-09-16T12:00:00Z\" }"));

        MvcResult result = mvc.perform(get("/vehicles/53/14/52.54/13")).andReturn();
        VehicleSearchResponse vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                VehicleSearchResponse.class);

        Assertions.assertEquals( 1 , vehicleResponse.getVehicleList().size() );

        mvc.perform(post("/vehicles/"+uuid+"/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"lat\": 52.53, \"lng\": 13.40, \"at\": \"2020-09-16T12:00:00Z\" }"));

        result = mvc.perform(get("/vehicles/53/14/52.54/13")).andReturn();
        vehicleResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                VehicleSearchResponse.class);

        Assertions.assertEquals( 0 , vehicleResponse.getVehicleList().size() );
    }

    @AfterEach
    public void cleanUp() {
        vehicleRepository.deleteAll();
        vehicleLogRepository.deleteAll();
        locationRepository.deleteAll();
        locationLogRepository.deleteAll();
    }
}
