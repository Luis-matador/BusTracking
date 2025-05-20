package com.busTracking.repositorios;

import com.busTracking.modelo.entidades.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusRepositorio extends JpaRepository<Bus, Long> {

    List<Bus> findByMarca(String marca);

    List<Bus> findByModelo(String modelo);

    List<Bus> findByMatricula(String matricula);

    @Query("SELECT b FROM Bus b WHERE b.ruta.id = :rutaId")
    List<Bus> buscarPorRutaId(@Param("rutaId") Long rutaId);

    @Query("SELECT b FROM Bus b WHERE b.conductor.id = :conductorId")
    List<Bus> buscarPorConductorId(@Param("conductorId") Long conductorId);

    @Query("SELECT COUNT(b) FROM Bus b WHERE b.ruta.id = :rutaId")
    Long contarBusesPorRuta(@Param("rutaId") Long rutaId);
}
