package com.busTracking.repositorios;

import com.busTracking.modelo.entidades.GPSData;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GPSDataRepositorio extends JpaRepository<GPSData, Long> {

    GPSData findTopByBusIdOrderByTiempoDesc(Long busId);


    @Query("SELECT g FROM GPSData g WHERE g.tiempo = " +
            "(SELECT MAX(g2.tiempo) FROM GPSData g2 WHERE g2.bus.id = g.bus.id)")
    List<GPSData> findLastGPSDataForAllBuses();


    @Query("SELECT g FROM GPSData g WHERE g.bus.id = :busId ORDER BY g.tiempo DESC LIMIT 1")
    GPSData findLastGPSDataForBus(@Param("busId") Long busId);



    List<GPSData> findByBusIdOrderByTiempoDesc(@Param("busId") Long busId);



    @Query("SELECT g FROM GPSData g WHERE g.bus.id = :busId AND g.tiempo BETWEEN :inicio AND :fin ORDER BY g.tiempo ASC")
    List<GPSData> findGPSDataByBusInTimeRange(@Param("busId") Long busId,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fin") LocalDateTime fin);

    GPSData findFirstByBusIdOrderByTiempoDesc(Long busId);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE gpsdata AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}