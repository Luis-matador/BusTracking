package com.busTracking.repositorios;

import com.busTracking.entidades.Parada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParadaRepositorio extends JpaRepository <Parada, Long>{

    List<Parada> findByRutaAsociadaId(Long id);

    List<Parada> findByRutaAsociadaIdOrderByOrdenAsc(Long rutaId);

    List<Parada> findByNombre(String nombre);

    @Query("SELECT p FROM Parada p WHERE " +
            "(ABS(p.latitud - :latitud) <= :margen AND ABS(p.longitud - :longitud) <= :margen)")
    List<Parada> findParadasCercanas(@Param("latitud") Double latitud,
                                     @Param("longitud") Double longitud,
                                     @Param("margen") Double margen);


}
