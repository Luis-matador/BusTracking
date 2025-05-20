package com.busTracking.servicios.impl;

import com.busTracking.modelo.entidades.Bus;
import com.busTracking.modelo.entidades.Parada;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.servicios.interfaces.BusServicio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusServicioImpl implements BusServicio {

    private final BusRepositorio busRepositorio;

    public BusServicioImpl(BusRepositorio busRepositorio) {
        this.busRepositorio = busRepositorio;
    }

    @Override
    public Bus crearBus(Bus bus) {

        if (bus.getMatricula() != null && !bus.getMatricula().isEmpty()) {
            List<Bus> busesExistentes = busRepositorio.findByMatricula(bus.getMatricula());
            if (!busesExistentes.isEmpty()) {
                throw new IllegalArgumentException("Ya existe un bus con la matrícula: " + bus.getMatricula());
            }
        }
        return busRepositorio.save(bus);
    }

    @Override
    public Bus obtenerBusPorId(Long id) {
        return busRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Bus con ID: " + id + " no encontrado"));
    }

    @Override
    public List<Bus> obtenerTodosLosBuses() {
        return busRepositorio.findAll();
    }

    @Override
    public Bus actualizarBus(Long id, Bus bus) {
        Optional<Bus> optionalBus = busRepositorio.findById(id);

        if (optionalBus.isPresent()) {
            Bus busExistente = optionalBus.get();

            busExistente.setMarca(bus.getMarca());
            busExistente.setModelo(bus.getModelo());
            busExistente.setCapacidad(bus.getCapacidad());

            if (bus.getMatricula() != null && !bus.getMatricula().equals(busExistente.getMatricula())) {
                List<Bus> busesConMismaMatricula = busRepositorio.findByMatricula(bus.getMatricula());
                if (!busesConMismaMatricula.isEmpty() &&
                        !id.equals(busesConMismaMatricula.get(0).getId())) {
                    throw new IllegalArgumentException("Ya existe un bus con la matrícula: " + bus.getMatricula());
                }
                busExistente.setMatricula(bus.getMatricula());
            }


            busExistente.setRuta(bus.getRuta());
            busExistente.setConductor(bus.getConductor());


            if (bus.getDatosGPS() != null) {
                busExistente.setDatosGPS(bus.getDatosGPS());
            }


            return busRepositorio.save(busExistente);

        } else {

            throw new EntityNotFoundException("Bus con ID: " + id + " no encontrado");
        }
    }


    @Override
    public void eliminarBus(Long id) {
        if (busRepositorio.existsById(id)) {
            busRepositorio.deleteById(id);
        } else {
            throw new EntityNotFoundException("Bus no encontrado con ID: " + id);
        }
    }

    @Override
    public List<Bus> obtenerBusesPorMarca(String marca) {
        return busRepositorio.findByMarca(marca);
    }

    @Override
    public List<Bus> obtenerBusesPorModelo(String modelo) {
        return busRepositorio.findByModelo(modelo);
    }

    @Override
    public List<Bus> obtenerBusesPorMatricula(String matricula) {
        return busRepositorio.findByMatricula(matricula);
    }

    @Override
    public List<Bus> buscarBusesPorRutaId(Long rutaId) {
        return busRepositorio.buscarPorRutaId(rutaId);
    }

    @Override
    public List<Bus> buscarBusesPorConductorId(Long conductorId) {
        return busRepositorio.buscarPorConductorId(conductorId);
    }

    @Override
    public Long contarBusesPorRuta(Long rutaId) {
        return busRepositorio.contarBusesPorRuta(rutaId);
    }

    @Override
    public boolean tieneBusesDisponibles(Long rutaId) {
        Long cantidad = contarBusesPorRuta(rutaId);
        return cantidad > 0;
    }
    public Parada encontrarSiguienteParada(List<Parada> paradasRuta, Double latitudActual, Double longitudActual) {
        if (paradasRuta == null || paradasRuta.isEmpty()) {
            return null;
        }

        Parada paradaMasCercana = null;
        double distanciaMinima = Double.MAX_VALUE;
        int indiceMasCercana = -1;

        // Encontrar la parada más cercana
        for (int i = 0; i < paradasRuta.size(); i++) {
            Parada parada = paradasRuta.get(i);
            double distancia = calcularDistancia(
                    latitudActual, longitudActual,
                    parada.getLatitud(), parada.getLongitud()
            );

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                paradaMasCercana = parada;
                indiceMasCercana = i;
            }
        }

        // Devolver la siguiente parada en la ruta
        if (indiceMasCercana < paradasRuta.size() - 1) {
            return paradasRuta.get(indiceMasCercana + 1);
        }

        // Si es la última parada, devolver null o la primera parada si es circular
        return null;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}