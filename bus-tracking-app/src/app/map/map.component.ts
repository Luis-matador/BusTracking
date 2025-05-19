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
  
  private marcadores: { [busId: number]: L.Marker } = {};
  
  public busesDisponibles: {id: number, nombre: string, linea: string}[] = [];
  public lineasDisponibles: string[] = [];
  public filtroForm: FormGroup;
  public seguimientoActivado: boolean = false;
  public busSeleccionadoId: number | null = null;
  public mapaCargado = false;
  
  private apiUrl = 'http://localhost:8080';
  public isBrowser: boolean;

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
      const mapContainer = document.getElementById('map');
      if (!mapContainer) {
        console.error('El contenedor del mapa no existe');
        return;
      }

      this.map = L.map('map', {
        zoomControl: false,
      }).setView([37.3943, -5.9332], 14);
      
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(this.map);
      
      L.control.zoom({ position: 'bottomright' }).addTo(this.map);
      L.control.scale({ position: 'bottomleft', imperial: false }).addTo(this.map);
      
      this.mapaCargado = true;
      
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
      
      const wsUrl = `ws://${window.location.hostname}:8080/ws-buses`;
      console.log('URL de conexión WebSocket:', wsUrl);
      
      this.stompClient = new Client({
        brokerURL: wsUrl,
        debug: (str) => {
          console.log('STOMP Debug:', str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      });

      if (window.location.protocol === 'http:') {
        console.log('Usando SockJS como fallback...');
        
        const socketUrl = `http://${window.location.hostname}:8080/ws-buses`;
        this.stompClient.configure({
          webSocketFactory: () => {
            return new SockJS(socketUrl) as any;
          }
        });
      }

      this.stompClient.onConnect = (frame) => {
        console.log('Conectado a WebSocket:', frame);
        
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

      if (this.marcadores[posicion.busId]) {
        const latlng = L.latLng(posicion.latitud, posicion.longitud);
        this.marcadores[posicion.busId].setLatLng(latlng);
        
        this.marcadores[posicion.busId].bindPopup(
          `<b>${posicion.busMatricula}</b><br>
           ${posicion.rutaNombre}<br>
           Velocidad: ${Math.round(posicion.velocidad)} km/h`
        );
      } else {
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

  private actualizarListaBusesDesdeDTO(posiciones: GPSDataDTO[]): void {
    const busesMap = new Map<number, {id: number, nombre: string, linea: string}>();
    const lineasSet = new Set<string>();
    
    posiciones.forEach(posicion => {
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

  public getBusNombre(busId: number | null): string {
    if (!busId) return '';
    const bus = this.busesDisponibles.find(b => b.id === busId);
    if (bus) {
      return `${bus.nombre} (Línea ${bus.linea})`;
    }
    return '';
  }
}