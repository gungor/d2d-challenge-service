package io.d2d.chservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.d2d.chservice.config.Config;
import io.d2d.chservice.controller.Controller;
import io.d2d.chservice.controller.ExceptionHandler;
import io.d2d.chservice.model.rest.ErrorResponse;
import io.d2d.chservice.repository.LocationLogRepository;
import io.d2d.chservice.repository.LocationRepository;
import io.d2d.chservice.repository.VehicleLogRepository;
import io.d2d.chservice.repository.VehicleRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
public class VehicleDeletionTest {

    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private VehicleRepository vehicleRepository;
    private VehicleLogRepository vehicleLogRepository;
    private LocationRepository locationRepository;
    private LocationLogRepository locationLogRepository;

    @Autowired
    public VehicleDeletionTest(MockMvc mvc,
                               VehicleRepository vehicleRepository,
                               VehicleLogRepository vehicleLogRepository,
                               LocationRepository locationRepository,
                               LocationLogRepository locationLogRepository) {
        this.mvc = mvc;
        this.vehicleRepository = vehicleRepository;
        this.vehicleLogRepository = vehicleLogRepository;
        this.locationRepository = locationRepository;
        this.locationLogRepository = locationLogRepository;
    }

    @Test
    public void shouldReturnErrorWhenVehicleNotExists() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        MvcResult result = mvc.perform(delete("/vehicles/"+uuid))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        Assertions.assertEquals("No vehicle exists for id: "+uuid,
                errorResponse.getErrorDescription() );
    }

    @Test
    public void shouldDeleteWhenVehicleExists() throws Exception {
        String uuid = "720a7414-0276-40d3-9b28-47970f86dd09";
        String requestJson = "{ \"id\": \""+uuid+"\" }";
        mvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isNoContent());

        requestJson = "{ \"lat\": 52.54, \"lng\": 13.41, \"at\": \"2017-09-01T12:00:00Z\" }";
        mvc.perform(post("/vehicles/"+uuid+"/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assertions.assertEquals(1, locationRepository.count());
        Assertions.assertEquals(1, vehicleRepository.count() );

        mvc.perform(delete("/vehicles/"+uuid))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, locationRepository.count());
        Assertions.assertEquals(0, vehicleRepository.count() );
    }

    @AfterEach
    public void cleanUp() {
        vehicleRepository.deleteAll();
        vehicleLogRepository.deleteAll();
        locationRepository.deleteAll();
        locationLogRepository.deleteAll();
    }

}
