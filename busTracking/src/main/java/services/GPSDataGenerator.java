package services;

import model.GPSData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GPSDataGenerator {

    public List<GPSData> generateGPSData() {
        List<GPSData> gpsDataList = new ArrayList<>();
        String [] busIds = {"BUS01","BUS02", "BUS03"};
        for(String busId : busIds){
            for(int i = 0; i < 60; i++){
                LocalDateTime timestamp= LocalDateTime.now().minusMinutes(60-i);
                double latitude= 40 + Math.random();
                double longitude = -3 + Math.random();
                double speed = Math.random() * 80;

                GPSData gpsData = new GPSData(0, busId, timestamp, latitude, longitude, speed);

                gpsDataList.add(gpsData);

            }
        }
        return gpsDataList;
    }
}
