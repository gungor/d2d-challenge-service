package io.d2d.chservice.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {

    @JsonProperty("errorDescription")
    private String errorDescription;
}
