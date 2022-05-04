package com.cedrus.tani.pingpong.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PongBall {
    private final String id;
    private PongTarget pongTarget;
    private Color color;

    public void flipBall(){
        this.pongTarget = pongTarget.equals(PongTarget.PING)?PongTarget.PONG:PongTarget.PING;
    }
}
