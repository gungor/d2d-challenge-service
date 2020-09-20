package io.d2d.chservice.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@ApiModel
public class RegisterVehicleRequest {

    @JsonProperty("id")
    @ApiModelProperty(name="id", value="id", required = true)
    @NotEmpty
    @Size(min = 1 , max = 36) // some id other than using uuid format, such as abc123 can be received
    private String id;

}
