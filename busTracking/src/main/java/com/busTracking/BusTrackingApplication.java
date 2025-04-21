package com.busTracking;

import model.GPSData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import services.GPSDataGenerator;
import services.GPSDataService;

import java.util.List;

@SpringBootApplication
public class BusTrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusTrackingApplication.class, args);

		GPSDataGenerator generator = new GPSDataGenerator();

		List<GPSData> simulatedData = generator.generateGPSData();

		GPSDataService service = new GPSDataService();
		service.writeToCSV(simulatedData);

	}

}
