package services;

import model.GPSData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GPSDataService {
    public void writeToCSV(List<GPSData> gpsDataList){
        //Crear archivo CSV
        try(FileWriter writer = new FileWriter("gps_data.csv")){
            //Escribir cabecera
            writer.write("id,busId,timestamp,latitude,longitude,speed\n");
            //Escribir registros
            for(GPSData gpsData : gpsDataList){
                String line= String.format("%d,%s,%s,%f,%f,%f\n",
                        gpsData.getId(),
                        gpsData.getBusId(),
                        gpsData.getTimestamp(),
                        gpsData.getLatitude(),
                        gpsData.getLongitude(),
                        gpsData.getSpeed());

                writer.write(line);
            }
            System.out.println("Archivo CSV creado");

        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo CSV");
        }
    }
}
