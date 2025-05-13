package com.busTracking.repositorios;

import com.busTracking.entidades.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RutaRepositorio extends JpaRepository<Ruta, Long> {

    Ruta findByNombre(String nombre);

    @Query("SELECT r FROM Ruta r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Ruta> findByNombreLike(@Param("nombre") String nombre);

    boolean existsByNombre(String nombre);

    @Query("SELECT r FROM Ruta r")
    List<Ruta> findAllBasicInfo();
}
