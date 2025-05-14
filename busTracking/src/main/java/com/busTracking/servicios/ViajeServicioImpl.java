package com.busTracking.servicios;

import com.busTracking.entidades.Bus;
import com.busTracking.entidades.Pasajero;
import com.busTracking.entidades.Ruta;
import com.busTracking.entidades.Viaje;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.repositorios.PasajeroRepositorio;
import com.busTracking.repositorios.RutaRepositorio;
import com.busTracking.repositorios.ViajeRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ViajeServicioImpl implements ViajeServicio {

    private final ViajeRepositorio viajeRepositorio;
    private final PasajeroRepositorio pasajeroRepositorio;
    private final BusRepositorio busRepositorio;
    private final RutaRepositorio rutaRepositorio;

    @Autowired
    public ViajeServicioImpl(ViajeRepositorio viajeRepositorio, PasajeroRepositorio pasajeroRepositorio, BusRepositorio busRepositorio, RutaRepositorio rutaRepositorio) {
        this.viajeRepositorio = viajeRepositorio;
        this.pasajeroRepositorio = pasajeroRepositorio;
        this.busRepositorio = busRepositorio;
        this.rutaRepositorio = rutaRepositorio;
    }

    @Override
    @Transactional
    public Viaje guardarViaje(Viaje viaje) {
        validarViaje(viaje);

        if (viaje.getInicio() == null) {
            viaje.setInicio(LocalDateTime.now());
        }

        validarRelaciones(viaje);

        return viajeRepositorio.save(viaje);
    }

    @Override
    public List<Viaje> obtenerTodosViajes() {
        return viajeRepositorio.findAll();
    }

    @Override
    public Page<Viaje> obtenerViajesPaginados(Pageable pageable) {
        return viajeRepositorio.findAll(pageable);
    }

    @Override
    public Optional<Viaje> obtenerViajePorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del viaje no puede ser nulo");
        }
        return viajeRepositorio.findById(id);
    }

    @Override
    public Viaje obtenerViajeObligatorioPorId(Long id) {
        return obtenerViajePorId(id).orElseThrow(() -> new EntityNotFoundException("Viaje con ID: " + id + " no encontrado"));
    }

    @Override
    @Transactional
    public Viaje actualizarViaje(Viaje viaje) {
        if (viaje.getId() == null) {
            throw new IllegalArgumentException("El ID del viaje no puede ser nulo para actualización");
        }

        Viaje viajeExistente = viajeRepositorio.findById(viaje.getId())
                .orElseThrow(() -> new EntityNotFoundException("Viaje con ID: " + viaje.getId() + " no encontrado"));

        validarViaje(viaje);

        if (viaje.getInicio() != null) {
            viajeExistente.setInicio(viaje.getInicio());
        }

        if (viaje.getFin() != null) {
            if (viaje.getFin().isBefore(viajeExistente.getInicio())) {
                throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de inicio");
            }
            viajeExistente.setFin(viaje.getFin());
        }

        if (viaje.getPasajero() != null) {
            Pasajero pasajero = pasajeroRepositorio.findById(viaje.getPasajero().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Pasajero con ID: " + viaje.getPasajero().getId() + " no encontrado"));
            viajeExistente.setPasajero(pasajero);
        }

        if (viaje.getBus() != null) {
            Bus bus = busRepositorio.findById(viaje.getBus().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Bus con ID: " + viaje.getBus().getId() + " no encontrado"));
            viajeExistente.setBus(bus);
        }

        if (viaje.getRuta() != null) {
            Ruta ruta = rutaRepositorio.findById(viaje.getRuta().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Ruta con ID: " + viaje.getRuta().getId() + " no encontrada"));
            viajeExistente.setRuta(ruta);
        }

        return viajeRepositorio.save(viajeExistente);
    }


    @Override
    @Transactional
    public void eliminarViaje(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del viaje no puede ser nulo");
        }

        if (!viajeRepositorio.existsById(id)) {
            throw new EntityNotFoundException("Viaje con ID: " + id + " no encontrado");
        }

        viajeRepositorio.deleteById(id);
    }

    @Override
    public List<Viaje> obtenerViajesPorPasajero(Long pasajeroId) {
        if (pasajeroId == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }

        if (!pasajeroRepositorio.existsById(pasajeroId)) {
            throw new EntityNotFoundException("Pasajero con ID: " + pasajeroId + " no encontrado");
        }

        return viajeRepositorio.findByPasajeroId(pasajeroId);
    }

    @Override
    public List<Viaje> obtenerViajesPorBus(Long busId) {
        if (busId == null) {
            throw new IllegalArgumentException("El ID del bus no puede ser nulo");
        }

        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("Bus con ID: " + busId + " no encontrado");
        }

        return viajeRepositorio.findByBusId(busId);
    }

    @Override
    public List<Viaje> obtenerViajesPorRuta(Long rutaId) {
        if (rutaId == null) {
            throw new IllegalArgumentException("El ID de la ruta no puede ser nulo");
        }

        if (!rutaRepositorio.existsById(rutaId)) {
            throw new EntityNotFoundException("Ruta con ID: " + rutaId + " no encontrada");
        }

        return viajeRepositorio.findByRutaId(rutaId);
    }

    @Override
    public List<Viaje> obtenerViajesEnProgreso() {
        return viajeRepositorio.findViajesEnProgreso();
    }

    @Override
    public Long contarViajesPorPasajero(Long pasajeroId) {
        if (pasajeroId == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }

        if (!pasajeroRepositorio.existsById(pasajeroId)) {
            throw new EntityNotFoundException("Pasajero con ID: " + pasajeroId + " no encontrado");
        }

        return viajeRepositorio.countViajesByPasajero(pasajeroId);
    }

    @Override
    @Transactional
    public Viaje iniciarViaje(Long pasajeroId, Long busId, Long rutaId) {
        if (pasajeroId == null || busId == null || rutaId == null) {
            throw new IllegalArgumentException("Los IDs de pasajero, bus y ruta no pueden ser nulos");
        }

        Pasajero pasajero = pasajeroRepositorio.findById(pasajeroId).orElseThrow(() -> new EntityNotFoundException("Pasajero con ID: " + pasajeroId + " no encontrado"));

        Bus bus = busRepositorio.findById(busId).orElseThrow(() -> new EntityNotFoundException("Bus con ID: " + busId + " no encontrado"));

        Ruta ruta = rutaRepositorio.findById(rutaId).orElseThrow(() -> new EntityNotFoundException("Ruta con ID: " + rutaId + " no encontrada"));

        if (tieneViajeEnProgreso(pasajeroId)) {
            throw new IllegalStateException("El pasajero ya tiene un viaje en progreso");
        }

        if (bus.getRuta() == null || !bus.getRuta().getId().equals(rutaId)) {
            throw new IllegalArgumentException("El bus con ID: " + busId + " no está asignado a la ruta con ID: " + rutaId);
        }

        Viaje viaje = new Viaje();
        viaje.setPasajero(pasajero);
        viaje.setBus(bus);
        viaje.setRuta(ruta);
        viaje.setInicio(LocalDateTime.now());

        Viaje viajeSalvado = viajeRepositorio.save(viaje);

        pasajero.setNumViajes(pasajero.getNumViajes() + 1);
        pasajeroRepositorio.save(pasajero);

        return viajeSalvado;
    }

    @Override
    @Transactional
    public Viaje finalizarViaje(Long viajeId) {
        if (viajeId == null) {
            throw new IllegalArgumentException("El ID del viaje no puede ser nulo");
        }

        Viaje viaje = viajeRepositorio.findById(viajeId)
                .orElseThrow(() -> new EntityNotFoundException("Viaje con ID: " + viajeId + " no encontrado"));

        if (viaje.getFin() != null) {
            throw new IllegalStateException("El viaje ya ha sido finalizado");
        }

        viaje.setFin(LocalDateTime.now());

        return viajeRepositorio.save(viaje);
    }

    @Override
    public List<Viaje> obtenerViajesEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }

        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        return viajeRepositorio.findAll().stream().filter(viaje -> {

                    LocalDateTime viajeInicio = viaje.getInicio();

                    return viajeInicio != null && !viajeInicio.isBefore(inicio) && !viajeInicio.isAfter(fin);}).toList();
    }

    @Override
    public long contarViajes() {
        return viajeRepositorio.count();
    }

    @Override
    public boolean tieneViajeEnProgreso(Long pasajeroId) {
        if (pasajeroId == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }

        if (!pasajeroRepositorio.existsById(pasajeroId)) {
            throw new EntityNotFoundException("Pasajero con ID: " + pasajeroId + " no encontrado");
        }

        List<Viaje> viajesEnProgreso = obtenerViajesEnProgreso();
        return viajesEnProgreso.stream().anyMatch(viaje -> viaje.getPasajero() != null && viaje.getPasajero().getId().equals(pasajeroId));
    }

    @Override
    @Transactional(readOnly = true)
    public Duration obtenerDuracionViaje(Long viajeId) {
        Viaje viaje = obtenerViajeObligatorioPorId(viajeId);

        if (viaje.getInicio() == null) {
            throw new IllegalStateException("El viaje no tiene hora de inicio registrada");
        }

        LocalDateTime finViaje = viaje.getFin();
        if (finViaje == null) {
            finViaje = LocalDateTime.now();
        }

        return Duration.between(viaje.getInicio(), finViaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Viaje> obtenerViajesCompletados() {
        return viajeRepositorio.findAll().stream().filter(viaje -> viaje.getFin() != null).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Viaje> obtenerUltimosViajesDePasajero(Long pasajeroId, int cantidad) {
        if (pasajeroId == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        if (!pasajeroRepositorio.existsById(pasajeroId)) {
            throw new EntityNotFoundException("Pasajero con ID: " + pasajeroId + " no encontrado");
        }

        return viajeRepositorio.findByPasajeroId(pasajeroId).stream()
                .sorted((v1, v2) -> v2.getInicio().compareTo(v1.getInicio())).limit(cantidad).toList();
    }


    private void validarViaje(Viaje viaje) {
        if (viaje == null) {
            throw new IllegalArgumentException("El viaje no puede ser nulo");
        }

        if (viaje.getInicio() != null && viaje.getFin() != null) {
            if (viaje.getFin().isBefore(viaje.getInicio())) {
                throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de inicio");
            }
        }
    }


    private void validarRelaciones(Viaje viaje) {
        if (viaje.getPasajero() == null) {
            throw new IllegalArgumentException("El pasajero es obligatorio");
        } else {
            if (!pasajeroRepositorio.existsById(viaje.getPasajero().getId())) {
                throw new EntityNotFoundException("Pasajero con ID: " + viaje.getPasajero().getId() + " no encontrado");
            }
        }

        if (viaje.getBus() == null) {
            throw new IllegalArgumentException("El bus es obligatorio");
        } else {
            if (!busRepositorio.existsById(viaje.getBus().getId())) {
                throw new EntityNotFoundException("Bus con ID: " + viaje.getBus().getId() + " no encontrado");
            }
        }

        if (viaje.getRuta() == null) {
            throw new IllegalArgumentException("La ruta es obligatoria");
        } else {
            if (!rutaRepositorio.existsById(viaje.getRuta().getId())) {
                throw new EntityNotFoundException("Ruta con ID: " + viaje.getRuta().getId() + " no encontrada");
            }
        }
    }

}