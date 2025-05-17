import { Component, OnInit, OnDestroy, PLATFORM_ID, Inject, AfterViewInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Client } from '@stomp/stompjs';
import * as L from 'leaflet';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import SockJS from 'sockjs-client';
import 'leaflet.marker.slideto';


interface GPSDataDTO {
  id: number;
  latitud: number;
  longitud: number;
  velocidad: number;
  direccion: number;
  tiempo: string;
  busId: number;
  busMatricula: string;
  rutaNombre: string;
}


interface GPSData {
  id: number;
  latitud: number;
  longitud: number;
  velocidad: number;
  direccion: number;
  tiempo: string;
  bus: {
    id: number;
    nombre: string;
    linea: string;
  };
}

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})

export class MapComponent implements OnInit, AfterViewInit, OnDestroy {
  private map: L.Map | null = null;
  private stompClient: Client | null = null;
  private busMarkers: Map<number, L.Marker> = new Map();
  private busRotations: Map<number, number> = new Map(); // Para guardar la rotación actual de cada bus
  
  // Mapa para almacenar los marcadores por ID de bus
  private marcadores: { [busId: number]: L.Marker } = {};
  
  public busesDisponibles: {id: number, nombre: string, linea: string}[] = [];
  public lineasDisponibles: string[] = [];
  public filtroForm: FormGroup;
  public seguimientoActivado: boolean = false;
  public busSeleccionadoId: number | null = null;
  public mapaCargado = false;
  
  // URL del backend - ajusta según tu configuración
  private apiUrl = 'http://localhost:8080';
  public isBrowser: boolean;

  // Configuración de animación
  private animationDuration = 1000; // duración de la animación en ms
  private lastPositions: Map<number, L.LatLng> = new Map(); // últimas posiciones conocidas

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private fb: FormBuilder
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    this.filtroForm = this.fb.group({
      linea: ['todos'],
      seguimientoBus: [null]
    });
  }

  ngOnInit(): void {
    // Configuración del formulario
    this.filtroForm.get('linea')?.valueChanges.subscribe(value => {
      this.filtrarPorLinea(value);
    });
    
    this.filtroForm.get('seguimientoBus')?.valueChanges.subscribe(value => {
      this.busSeleccionadoId = value;
      this.seguimientoActivado = !!value;
      
      if (this.seguimientoActivado && this.busSeleccionadoId && this.map) {
        const marker = this.busMarkers.get(this.busSeleccionadoId);
        if (marker) {
          this.map.setView(marker.getLatLng(), 16);
        }
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.isBrowser) {
      // Inicializar el mapa después de que la vista se haya cargado
      setTimeout(() => {
        this.initMap();
      }, 100);
    }
  }

  ngOnDestroy(): void {
    this.desconectarWebSocket();
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    try {
      // Asegurar que el contenedor del mapa existe
      const mapContainer = document.getElementById('map');
      if (!mapContainer) {
        console.error('El contenedor del mapa no existe');
        return;
      }

      // Inicializar el mapa en Sevilla Este
      this.map = L.map('map', {
        zoomControl: false,
      }).setView([37.3943, -5.9332], 14);
      
      // Añadir la capa de mosaicos
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(this.map);
      
      // Añadir controles adicionales
      L.control.zoom({ position: 'bottomright' }).addTo(this.map);
      L.control.scale({ position: 'bottomleft', imperial: false }).addTo(this.map);
      
      // Marcar que el mapa se ha cargado
      this.mapaCargado = true;
      
      // Conectar al WebSocket después de que el mapa se haya inicializado
      if (this.isBrowser) {
        this.conectarWebSocket();
      }
      
      console.log('Mapa inicializado correctamente');
    } catch (error) {
      console.error('Error al inicializar el mapa:', error);
    }
  }

  private conectarWebSocket(): void {
    if (!isPlatformBrowser(this.platformId)) {
      console.log('No se intenta conectar WebSocket en el servidor');
      return;
    }
    if (!this.isBrowser) return;

    try {
      console.log('Intentando conectar al WebSocket...');
      
      // URL del WebSocket
      const wsUrl = `ws://${window.location.hostname}:8080/ws-buses`;
      console.log('URL de conexión WebSocket:', wsUrl);
      
      // Configurar el cliente Stomp directamente con la URL WebSocket
      this.stompClient = new Client({
        brokerURL: wsUrl,
        debug: (str) => {
          console.log('STOMP Debug:', str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      });

      // Fallback para navegadores que no soporten WebSocket nativo
      // Cambiar a esta configuración si la conexión directa no funciona
      if (window.location.protocol === 'http:') {
        console.log('Usando SockJS como fallback...');
        
        // Sobrescribir la configuración para usar SockJS
        const socketUrl = `http://${window.location.hostname}:8080/ws-buses`;
        this.stompClient.configure({
          webSocketFactory: () => {
            return new SockJS(socketUrl) as any;
          }
        });
      }

      this.stompClient.onConnect = (frame) => {
        console.log('Conectado a WebSocket:', frame);
        
        // Suscribirse al canal de posiciones de buses
        this.stompClient?.subscribe('/topic/posiciones-buses', (message) => {
          try {
            console.log('Mensaje recibido:', message.body);
            const posiciones: GPSDataDTO[] = JSON.parse(message.body);
            this.actualizarPosicionesBuses(posiciones);
            this.actualizarListaBusesDesdeDTO(posiciones);
          } catch (error) {
            console.error('Error al procesar mensaje:', error);
          }
        });
        
        // Solicitar posiciones iniciales al conectar
        console.log('Solicitando posiciones iniciales...');
        this.stompClient?.publish({
          destination: '/app/solicitar-posiciones',
          body: JSON.stringify({})
        });
      };

      this.stompClient.onStompError = (frame) => {
        console.error('Error en STOMP:', frame);
      };

      this.stompClient.onWebSocketError = (event) => {
        console.error('Error en WebSocket:', event);
      };

      this.stompClient.onDisconnect = () => {
        console.log('Desconectado de WebSocket');
      };

      console.log('Activando conexión WebSocket...');
      this.stompClient.activate();
    } catch (error) {
      console.error('Error al configurar WebSocket:', error);
    }
  }

  private desconectarWebSocket(): void {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate();
    }
  }

  
  actualizarPosicionesBuses(posiciones: GPSDataDTO[]): void {
    if (!posiciones || posiciones.length === 0) {
      console.log('No hay posiciones para actualizar');
      return;
    }

    posiciones.forEach(posicion => {
      if (!posicion || typeof posicion.busId === 'undefined') {
        console.error('Posición de bus inválida:', posicion);
        return;
      }

      // Actualizar o crear marcador
      if (this.marcadores[posicion.busId]) {
        const latlng = L.latLng(posicion.latitud, posicion.longitud);
        this.marcadores[posicion.busId].setLatLng(latlng);
        
        // Actualiza también el popup
        this.marcadores[posicion.busId].bindPopup(
          `<b>${posicion.busMatricula}</b><br>
           ${posicion.rutaNombre}<br>
           Velocidad: ${Math.round(posicion.velocidad)} km/h`
        );
      } else {
        // Si no existe, crear nuevo marcador
        console.log(`Creando nuevo marcador para el bus ID: ${posicion.busId}`);
        
        if (!this.map) {
          console.error('El mapa no está inicializado');
          return;
        }
        
        const icono = L.icon({
          iconUrl: 'assets/bus-icons/bus-icon.svg',
          iconSize: [32, 32],
          iconAnchor: [16, 16],
          popupAnchor: [0, -16]
        });
        
        const marcador = L.marker([posicion.latitud, posicion.longitud], { icon: icono })
          .bindPopup(
            `<b>${posicion.busMatricula}</b><br>
             ${posicion.rutaNombre}<br>
             Velocidad: ${Math.round(posicion.velocidad)} km/h`
          )
          .addTo(this.map);
        
        this.marcadores[posicion.busId] = marcador;
        this.busMarkers.set(posicion.busId, marcador);
      }
    });
  }

  // Método para actualizar la lista de buses desde el nuevo formato DTO
  private actualizarListaBusesDesdeDTO(posiciones: GPSDataDTO[]): void {
    const busesMap = new Map<number, {id: number, nombre: string, linea: string}>();
    const lineasSet = new Set<string>();
    
    posiciones.forEach(posicion => {
      // Extraer identificador de línea del nombre de la ruta
      const lineaMatch = posicion.rutaNombre.match(/Línea\s+([^:]+)/);
      const linea = lineaMatch ? lineaMatch[1].trim() : 'Desconocida';
      
      busesMap.set(posicion.busId, { 
        id: posicion.busId, 
        nombre: posicion.busMatricula, 
        linea: linea 
      });
      
      lineasSet.add(linea);
    });
    
    this.busesDisponibles = Array.from(busesMap.values());
    this.lineasDisponibles = Array.from(lineasSet);
  }

  private crearIconoBus(linea: string, rotacion: number): L.DivIcon {
    const color = this.obtenerColorLinea(linea);
    
    return L.divIcon({
      html: `
        <div class="bus-marker" style="background-color: ${color}; transform: rotate(${rotacion}deg)">
          <img src="assets/bus-icons/bus-icon.svg" alt="Bus" class="bus-icon">
          <span class="linea-numero">${linea}</span>
        </div>
      `,
      className: 'custom-div-icon',
      iconSize: [40, 40],
      iconAnchor: [20, 20]
    });
  }

  private actualizarIconoRotado(marker: L.Marker, linea: string, rotacion: number): void {
    const icon = this.crearIconoBus(linea, rotacion);
    marker.setIcon(icon);
  }

  private actualizarListaBuses(posiciones: GPSData[]): void {
    // Actualizar lista de buses disponibles
    const busesMap = new Map<number, {id: number, nombre: string, linea: string}>();
    const lineasSet = new Set<string>();
    
    posiciones.forEach(posicion => {
      const bus = posicion.bus;
      busesMap.set(bus.id, { id: bus.id, nombre: bus.nombre, linea: bus.linea });
      lineasSet.add(bus.linea);
    });
    
    this.busesDisponibles = Array.from(busesMap.values());
    this.lineasDisponibles = Array.from(lineasSet);
  }

  private crearContenidoPopup(posicion: GPSData): string {
    const fecha = new Date(posicion.tiempo).toLocaleString();
    return `
      <div class="bus-popup">
        <h3>Bus ${posicion.bus.nombre}</h3>
        <p><strong>Línea:</strong> ${posicion.bus.linea}</p>
        <p><strong>Velocidad:</strong> ${posicion.velocidad.toFixed(1)} km/h</p>
        <p><strong>Última actualización:</strong> ${fecha}</p>
        <button class="seguir-bus" data-bus-id="${posicion.bus.id}">Seguir este bus</button>
      </div>
    `;
  }

  private obtenerColorLinea(linea: string): string {
    const colores = [
      '#FF5733', '#33FF57', '#3357FF', '#F033FF', '#FF33A1',
      '#33FFF5', '#F5FF33', '#FF8C33', '#8C33FF', '#33FFCE'
    ];
    
    const indice = parseInt(linea.replace(/\D/g, ''), 10) % colores.length;
    return colores[indice] || '#3388ff';
  }

  public filtrarPorLinea(linea: string): void {
    if (!this.map) return;

    this.busMarkers.forEach((marker, busId) => {
      const bus = this.busesDisponibles.find(b => b.id === busId);
      
      if (linea === 'todos' || (bus && bus.linea === linea)) {
        marker.addTo(this.map!);
      } else {
        marker.remove();
      }
    });
  }

  public seleccionarBus(busId: number): void {
    this.filtroForm.get('seguimientoBus')?.setValue(busId);
  }

  public desactivarSeguimiento(): void {
    this.filtroForm.get('seguimientoBus')?.setValue(null);
    this.seguimientoActivado = false;
  }

  // Nueva función para obtener el nombre del bus seleccionado para mostrar en la UI
  public getBusNombre(busId: number | null): string {
    if (!busId) return '';
    const bus = this.busesDisponibles.find(b => b.id === busId);
    if (bus) {
      return `${bus.nombre} (Línea ${bus.linea})`;
    }
    return '';
  }
}