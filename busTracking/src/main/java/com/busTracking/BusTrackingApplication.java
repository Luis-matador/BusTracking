package com.busTracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BusTrackingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BusTrackingApplication.class, args);
		System.out.println("Aplicación iniciada correctamente");
	}
}