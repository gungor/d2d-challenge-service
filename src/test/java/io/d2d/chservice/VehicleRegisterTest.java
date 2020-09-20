package io.d2d.chservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.d2d.chservice.config.Config;
import io.d2d.chservice.controller.Controller;
import io.d2d.chservice.controller.ExceptionHandler;
import io.d2d.chservice.model.db.Vehicle;
import io.d2d.chservice.model.rest.ErrorResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class VehicleRegisterTest {

    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private VehicleRepository vehicleRepository;
    private VehicleLogRepository vehicleLogRepository;
    private LocationRepository locationRepository;
    private LocationLogRepository locationLogRepository;
    private VehicleService vehicleService;

    @Autowired
    public VehicleRegisterTest(MockMvc mvc,
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
    public void shouldReturnBadRequestWhenEmptyUUIDReceived() throws Exception {
        String requestJson = "{ \"id\": \"\" }";

        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenIdLongerThanUUIDFormatReceived() throws Exception {
        String requestJson = "{ \"id\": \"e26be929-3de1-47cc-9bdc-d0a10ea56240-32\" }";

        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRecordVehicle() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        String requestJson = "{ \"id\": \""+uuid+"\" }";

        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isNoContent());

        Vehicle vehicle = vehicleRepository.getVehicleByUUID(uuid).get();
        Assertions.assertNotNull(vehicle);
    }



    @Test
    public void shouldReturnErrorWhenVehicleUuidExists() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        vehicleService.saveVehicle(uuid);
        String requestJson = "{ \"id\": \""+uuid+"\" }";

        MvcResult result = mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponse.class);
        Assertions.assertEquals( "Vehicle with id: "+ uuid + " already exists",
                errorResponse.getErrorDescription() );
    }

    @AfterEach
    public void cleanUp() {
        vehicleRepository.deleteAll();
        vehicleLogRepository.deleteAll();
        locationRepository.deleteAll();
        locationLogRepository.deleteAll();
    }
}
