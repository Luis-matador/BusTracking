package com.busTracking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusTrackingApplication {

	private static final Logger log = LoggerFactory.getLogger(BusTrackingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BusTrackingApplication.class, args);
		log.info("Aplicación iniciada correctamente");
	}
}
