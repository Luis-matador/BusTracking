package com.busTracking.repositorios;

import com.busTracking.entidades.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViajeRepositorio extends JpaRepository<Viaje, Long> {

    List<Viaje> findByPasajeroId(Long pasajeroId);

    List<Viaje> findByBusId(Long busId);

    List<Viaje> findByRutaId(Long rutaId);

    @Query("SELECT v FROM Viaje v WHERE v.fin IS NULL")
    List<Viaje> findViajesEnProgreso();

    @Query("SELECT COUNT(v) FROM Viaje v WHERE v.pasajero.id = :pasajeroId")
    Long countViajesByPasajero(@Param("pasajeroId") Long pasajeroId);
}
