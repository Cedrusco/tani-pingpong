package com.cedrus.tani.pingpong.stream;

import com.cedrus.tani.pingpong.config.AppConfig;
import com.cedrus.tani.pingpong.config.TopicConfig;
import com.cedrus.tani.pingpong.model.PongBall;
import com.cedrus.tani.pingpong.model.PongTarget;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopologyProvider {
    private final TopicConfig topicConfig;
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public TopologyProvider(TopicConfig topicConfig, AppConfig appConfig, ObjectMapper objectMapper){
        this.topicConfig = topicConfig;
        this.appConfig = appConfig;
        this.objectMapper = objectMapper;
    }

    public Topology getPingPongTopology(PongTarget pongTarget){
        final StreamsBuilder builder =new StreamsBuilder();
        log.info("Starting to build the stream now.");

        final KStream<String, String> initialStream = builder.stream(topicConfig.getTopicName(),
                Consumed.with(Serdes.String(), Serdes.String()));

        final KStream<String,String>[] branches = initialStream.branch(getTargetFilterPredicate(pongTarget));
        final KStream<String,String> filteredStream = branches[0];

        final KStream<String,String> loggedAndDelayedStream = filteredStream.transformValues(getLogAndDelayVts());
        loggedAndDelayedStream.to(topicConfig.getTopicName(), Produced.with(Serdes.String(),Serdes.String()));
        return builder.build();
    }

    private Predicate<String,String> getTargetFilterPredicate(PongTarget pongTarget){
        return (key, value) -> {
            PongBall pongBall = deserializeBall(value);
            return pongBall.getPongTarget().equals(pongTarget);
        };
    }

    private PongBall deserializeBall(String pongBallAsString){
        try{
            return objectMapper.readValue(pongBallAsString, PongBall.class);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private String serializeBall(PongBall pongBall){
        try{
            return objectMapper.writeValueAsString(pongBall);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private ValueTransformerSupplier<String, String> getLogAndDelayVts(){
        return()->
        new ValueTransformer<String, String>() {
            @Override
            public void init(ProcessorContext context) {

            }

            @Override
            public String transform(String value) {
                log.info("Transforming ball - Value is: {}", value);
                log.debug("Received ball.");
                final int minDelaySec = appConfig.getMinDelaySeconds();
                final int maxDelaySec = appConfig.getMaxDelaySeconds();
                final int deltaDelaySec = maxDelaySec- minDelaySec;
                final Random random = new Random();
                final int sleepSecs = random.nextInt(deltaDelaySec) + minDelaySec;
                log.debug("Will Sleep for {} seconds.", sleepSecs);
                try{
                    Thread.sleep(sleepSecs*1000L);
                    return value;
                }catch(InterruptedException iex){
                    log.error("Interrupted during sleep.", iex);
                }

                final PongBall pongBall= deserializeBall(value);
                pongBall.flipBall();
                return serializeBall(pongBall);
            }

            @Override
            public void close() {

            }
        };
    }

}
