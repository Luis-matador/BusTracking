package com.busTracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusTrackingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BusTrackingApplication.class, args);
		System.out.println("Aplicación iniciada correctamente");
	}
}