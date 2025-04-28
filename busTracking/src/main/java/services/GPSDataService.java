package services;

import model.GPSData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.GPSDataRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GPSDataService {

    private final GPSDataRepository gpsDataRepository;

    @Autowired
    public GPSDataService(GPSDataRepository gpsDataRepository) {
        this.gpsDataRepository = gpsDataRepository;
    }

    public List<GPSData> getAllGPSData() {
        return gpsDataRepository.findAll();
    }

    public Optional<GPSData> getGPSDataById(Long id) {
        return gpsDataRepository.findById(id);
    }

    public GPSData saveGPSData(GPSData gpsData) {
        return gpsDataRepository.save(gpsData);
    }

    public void deleteGPSData(Long id) {
        gpsDataRepository.deleteById(id);
    }
}