package com.cedrus.tani.pingpong.stream;

import com.cedrus.tani.pingpong.config.KafkaConfig;

import java.util.Properties;

import com.cedrus.tani.pingpong.model.PongTarget;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PongService {

    private final KafkaConfig kafkaConfig;
    private final TopologyProvider topologyProvider;

    @Autowired
    public PongService(KafkaConfig kafkaConfig, TopologyProvider topologyProvider){
        this.kafkaConfig = kafkaConfig;
        this.topologyProvider = topologyProvider;
    }

    public void startPongStream(){

        final Properties kafkaProperties = new Properties();
        kafkaProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getKafkaAppId()+ PongTarget.PONG);
        kafkaProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaConfig.getBootstrapServers());
        kafkaProperties.put("AUTO_OFFSET_RESET_CONFIG", kafkaConfig.getAutoOffsetReset());
        final KafkaStreams pongStream = new KafkaStreams(topologyProvider.getPingPongTopology(PongTarget.PING),
                kafkaProperties);
        pongStream.start();
        log.info("Pong Stream Started");
        Runtime.getRuntime().addShutdownHook(new Thread(pongStream::close));
    }
}
