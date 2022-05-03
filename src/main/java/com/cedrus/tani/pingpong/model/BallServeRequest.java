package com.cedrus.tani.pingpong.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class BallServeRequest {
    @JsonProperty
    private String id;
    @JsonProperty
    private String ballName;
    @JsonProperty
    private String color;
}
