package com.cedrus.tani.pingpong;

import com.cedrus.tani.pingpong.config.AppConfig;
import com.cedrus.tani.pingpong.config.KafkaConfig;
import com.cedrus.tani.pingpong.config.TopicConfig;
import com.cedrus.tani.pingpong.stream.PingService;
import com.cedrus.tani.pingpong.stream.PongService;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.apache.kafka.common.requests.FetchMetadata.log;


@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class, KafkaConfig.class, TopicConfig.class})
public class PingPongApplication {

	public static final Marker FATAL_MARKER = MarkerFactory.getMarker("FATAL");
	public static void main(String[] args) {
		SpringApplication.run(PingPongApplication.class, args);
	}
	@Bean
	public CommandLineRunner pingRunner(ApplicationContext context){
		try{
			return args ->{
				log.info("Will run stream of Kafka Messages now.");
				((PingService) context.getBean("pingService")).startPingStream();
			};
		}catch(RuntimeException e){
			log.error(FATAL_MARKER,
					"RuntimeException encountered when trying to start the service using CommandLineRunner message={}",
					e.getMessage());
			log.error("RuntimeException", e);
			throw e;
		}
	}

	public CommandLineRunner pongRunner(ApplicationContext context){
		try{
			return args ->{
				log.info("Will run stream of Kafka Messages now.");
				((PongService) context.getBean("pongService")).startPongStream();
			};
		}catch(RuntimeException e){
			log.error(FATAL_MARKER,
					"RuntimeException encountered when trying to start the service using CommandLineRunner message={}",
					e.getMessage());
			log.error("RuntimeException", e);
			throw e;
		}
	}
}
