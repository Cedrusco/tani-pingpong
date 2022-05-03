package com.cedrus.tani.pingpong.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ApiResponse {
    @JsonProperty("SuccessInd")
    private boolean successInd;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Code")
    private String code;
}
