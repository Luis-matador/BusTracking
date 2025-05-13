package com.busTracking.repositorios;

import com.busTracking.entidades.GPSData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar los datos GPS de los autobuses.
 */
public interface GPSDataRepositorio extends JpaRepository<GPSData, Long> {


     //Encuentra los datos GPS más recientes de todos los autobuses.
    @Query("SELECT g FROM GPSData g WHERE g.tiempo = " +
            "(SELECT MAX(g2.tiempo) FROM GPSData g2 WHERE g2.bus.id = g.bus.id)")
    List<GPSData> findLastGPSDataForAllBuses();

    // Encuentra el dato GPS más reciente de un autobús específico.
    @Query("SELECT g FROM GPSData g WHERE g.bus.id = :busId ORDER BY g.tiempo DESC LIMIT 1")
    GPSData findLastGPSDataForBus(@Param("busId") Long busId);


     // Encuentra todos los datos GPS históricos para un autobús específico.
    List<GPSData> findByBusIdOrderByTiempoDesc(@Param("busId") Long busId);


     // Encuentra los datos GPS en un rango de tiempo específico para un autobús.
    @Query("SELECT g FROM GPSData g WHERE g.bus.id = :busId AND g.tiempo BETWEEN :inicio AND :fin ORDER BY g.tiempo ASC")
    List<GPSData> findGPSDataByBusInTimeRange(@Param("busId") Long busId,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fin") LocalDateTime fin);
}