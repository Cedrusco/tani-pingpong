package com.cedrus.tani.pingpong.controller;

import com.cedrus.tani.pingpong.model.BallServeRequest;
import com.cedrus.tani.pingpong.model.BallServeResponse;
import com.cedrus.tani.pingpong.model.Color;
import com.cedrus.tani.pingpong.model.PongBall;
import com.cedrus.tani.pingpong.model.PongTarget;
import com.cedrus.tani.pingpong.stream.PongBallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class PingPongBallController {

    private final PongBallService pongBallService;

    @Autowired
   public PingPongBallController(PongBallService pongBallService){
        this.pongBallService=pongBallService;
    }

    @PostMapping(value= "/ball")
    public ResponseEntity<BallServeResponse> throwBall(
            @RequestBody BallServeRequest ballServeRequest){
        log.debug(
                "ThrowBall init, request={}",
                ballServeRequest);

        if(false){
            return new ResponseEntity<>(
                    new BallServeResponse(
                            false,
                            String.format("Unrecognized ball color : %s", ballServeRequest.getColor()),
                            "INVALID COLOR"),
                    HttpStatus.BAD_REQUEST);
        }else{
            return addBall(ballServeRequest);
        }
    }

    private ResponseEntity<BallServeResponse> addBall(BallServeRequest ballServeRequest) {
        try{
            final PongBall pongBall =
                    new PongBall(
                            ballServeRequest.getId(),
                            PongTarget.PING,
                            Color.valueOf(ballServeRequest.getColor())
                            );
            pongBallService.addBall(pongBall);
            BallServeResponse ballServeResponse = new BallServeResponse(true);
            return new ResponseEntity<>(ballServeResponse, HttpStatus.OK);

        }catch(RuntimeException e){
            log.error("RuntimeException caught when trying to enqueue ball.",e);
            BallServeResponse ballServeResponse= new BallServeResponse(false, e.getMessage(),"INTERNAL_ERROR");
            return new ResponseEntity<>(ballServeResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
