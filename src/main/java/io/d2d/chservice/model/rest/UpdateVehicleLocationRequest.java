package io.d2d.chservice.model.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.d2d.chservice.model.rest.deserializer.JsonTimeDeserializerWithCetTimeZone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel
public class UpdateVehicleLocationRequest {

    @JsonProperty("lat")
    @ApiModelProperty(name="lat", value="lat", required = true)
    @NotNull
    @Min(-90)
    @Max(90)
    private Double lat;

    @JsonProperty("lng")
    @ApiModelProperty(name="lng", value="lng", required = true)
    @NotNull
    @Min(-180)
    @Max(180)
    private Double lng;

    @NotNull
    @JsonProperty("at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonDeserialize(using = JsonTimeDeserializerWithCetTimeZone.class)
    @ApiModelProperty(dataType = "java.lang.String", example = "2017-12-02T12:00:00+01:00")
    private Date at;

}
