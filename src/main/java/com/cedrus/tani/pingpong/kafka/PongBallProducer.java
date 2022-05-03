package com.cedrus.tani.pingpong.kafka;

import com.cedrus.tani.pingpong.config.KafkaConfig;
import com.cedrus.tani.pingpong.config.TopicConfig;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PongBallProducer {
    private final TopicConfig topicConfig;
    private final Producer<String, String> producer;

    public PongBallProducer(KafkaConfig kafkaConfig, TopicConfig topicConfig){
        this.topicConfig = topicConfig;

        Serializer<String> stringSerializer = Serdes.String().serializer();
        final Properties kafkaProperties = new Properties();
        kafkaProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getKafkaAppId());
        kafkaProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(kafkaProperties, stringSerializer, stringSerializer);
    }
    public void sendMessage(String event){
        log.info("Sending event={}", event);

        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(
                        topicConfig.getTopicName(),
                        null,
                        event
                );
        producer.send(producerRecord);
        producer.close();
    }
}
