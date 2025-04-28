package repository;

import model.GPSData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GPSDataRepository extends JpaRepository<GPSData, Long> {
}