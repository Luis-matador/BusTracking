package com.busTracking.repositorios;

import com.busTracking.entidades.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConductorRepositorio extends JpaRepository<Conductor, Long> {

    List<Conductor> findByNombre(String nombre);

    List<Conductor> findByApellido(String apellido);

    boolean existsByDni(String dni);

    List<Conductor> findByDni(String dni);

    @Query("SELECT c FROM Conductor c WHERE c.nombre LIKE %:cadena% OR c.apellido LIKE %:cadena%")
    List<Conductor> buscarPorNombreOApeParcial(@Param("cadena") String cadena);

    @Query("SELECT c FROM Conductor c WHERE c.busAsignado.id = :busId")
    Optional<Conductor> findConductorByBus(@Param("busId") Long busId);
}
