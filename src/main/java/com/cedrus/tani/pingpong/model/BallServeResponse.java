package com.cedrus.tani.pingpong.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BallServeResponse {
    @JsonProperty
    private ApiResponse apiResponse;

    public BallServeResponse(boolean successInd){
        if(successInd){
            this.apiResponse = new ApiResponse();
            this.apiResponse.setCode("0");
            this.apiResponse.setMessage("Success");
            this.apiResponse.setSuccessInd(true);
        }else{
            this.apiResponse = new ApiResponse();
            this.apiResponse.setCode("-1");
            this.apiResponse.setMessage("Failure");
            this.apiResponse.setSuccessInd(false);
        }
    }

    public BallServeResponse(boolean successInd, String message, String code){
        this.apiResponse= new ApiResponse();
        this.apiResponse.setCode(code);
        this.apiResponse.setMessage(message);
        this.apiResponse.setSuccessInd(successInd);
    }

}
