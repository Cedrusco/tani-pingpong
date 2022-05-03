package com.cedrus.tani.pingpong.stream;

import com.cedrus.tani.pingpong.kafka.PongBallProducer;
import com.cedrus.tani.pingpong.model.PongBall;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class PongBallService {
    private final PongBallProducer pongBallProducer;
    private final ObjectMapper objectMapper;

    public PongBallService(PongBallProducer pongBallProducer, ObjectMapper objectMapper){
        this.pongBallProducer = pongBallProducer;
        this.objectMapper = objectMapper;
    }

    public void addBall(PongBall pongBall){
        try{
            String ballAsJson = objectMapper.writeValueAsString(pongBall);
            pongBallProducer.sendMessage(ballAsJson);
        }catch( Exception e){
            throw new RuntimeException(e);
        }
    }

}
