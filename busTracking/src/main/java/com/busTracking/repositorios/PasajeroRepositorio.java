package com.busTracking.repositorios;

import com.busTracking.entidades.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PasajeroRepositorio extends JpaRepository<Pasajero, Long> {

    Pasajero findByTelefono(String telefono);

    @Query("SELECT p FROM Pasajero p WHERE p.numViajes > :viajes")
    List<Pasajero> findByNumViajesMayorQue(@Param("viajes") Integer viajes);

    @Query("SELECT p FROM Pasajero p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
            "OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    List<Pasajero> findByNombreOrApellido(@Param("nombre") String nombre,
                                          @Param("apellido") String apellido);

    @Query("SELECT p FROM Pasajero p ORDER BY p.numViajes DESC")
    List<Pasajero> findAllOrderByNumViajesDesc();

}
