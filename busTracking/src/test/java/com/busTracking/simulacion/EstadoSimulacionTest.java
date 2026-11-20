package com.busTracking.simulacion;

import com.busTracking.modelo.entidades.Bus;
import com.busTracking.modelo.entidades.Parada;
import com.busTracking.modelo.simulacion.EstadoSimulacion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class EstadoSimulacionTest {

    private static Parada parada(long id, double latitud, double longitud) {
        Parada p = new Parada();
        p.setId(id);
        p.setLatitud(latitud);
        p.setLongitud(longitud);
        return p;
    }

    // Dos paradas separadas ~1 km en dirección norte (0,009º de latitud)
    private final Parada inicio = parada(1L, 37.3891, -5.9845);
    private final Parada fin = parada(2L, 37.3981, -5.9845);

    @Test
    void aMitadDeCamino_elProgresoEsAproximadamenteLaMitad() {
        EstadoSimulacion estado = new EstadoSimulacion(new Bus(), List.of(inicio, fin));

        // 30 km/h durante 60 s = 500 m de un segmento de ~1000 m
        estado.actualizarProgreso(60, 30);

        assertThat(estado.getProgresoSegmento()).isCloseTo(0.5, within(0.01));
        assertThat(estado.getIndiceParadaActual()).isZero();
        assertThat(estado.isLlegadaParada()).isFalse();
    }

    @Test
    void alCompletarElSegmento_avanzaALaSiguienteParada() {
        EstadoSimulacion estado = new EstadoSimulacion(new Bus(), List.of(inicio, fin));

        estado.actualizarProgreso(60, 30);
        estado.actualizarProgreso(61, 30);

        assertThat(estado.isLlegadaParada()).isTrue();
        assertThat(estado.getIndiceParadaActual()).isEqualTo(1);
        assertThat(estado.getProgresoSegmento()).isZero();
    }

    @Test
    void trasLaUltimaParada_vuelveAlPrincipioDeLaRuta() {
        EstadoSimulacion estado = new EstadoSimulacion(new Bus(), List.of(inicio, fin));

        estado.actualizarProgreso(200, 30); // llega a la parada 2
        estado.actualizarProgreso(200, 30); // vuelve a la parada 1

        assertThat(estado.getIndiceParadaActual()).isZero();
        assertThat(estado.getParadaSiguiente()).isEqualTo(fin);
    }
}
