package model;

import java.time.LocalDateTime;

public class GPSData {
    private int id;
    private String busId;
    private LocalDateTime timestamp;
    private double latitude;
    private double longitude;
    private double speed;

    // Necesario para frameworks
    public GPSData() {}


    public GPSData(int id, String busId, LocalDateTime timestamp, double latitude, double longitude, double speed) {
        this.id = id;
        this.busId = busId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }


    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getBusId() { return busId; }

    public void setBusId(String busId) { this.busId = busId; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getSpeed() { return speed; }

    public void setSpeed(double speed) { this.speed = speed; }


    @Override
    public String toString() {
        return "GPSData{ id=" + getId() + ", busId=" + getBusId() + ", timestamp=" + getTimestamp() + ", latitude=" + getLatitude() + ", longitude=" + getLongitude() + ", speed=" + getSpeed()+")";
    }
}
