import { Injectable, OnDestroy } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject, timer } from 'rxjs';
import { GPSDataDTO } from '../models/gps-data-dto.model';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements OnDestroy {
  private stompClient: Client | null = null;
  private posicionesBuses = new BehaviorSubject<GPSDataDTO[]>([]);
  private connected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private readonly RECONNECT_INTERVAL = 5000;
  private connectionState = new BehaviorSubject<boolean>(false);

  constructor() {
    this.initializeConnection();
  }

  private initializeConnection() {
    if (this.connected) return;

    console.log('Iniciando conexión WebSocket...');
    try {
      const socket = new SockJS('http://localhost:8080/ws-buses');
      this.stompClient = new Client();
      
      this.stompClient.webSocketFactory = () => socket as any;
      this.stompClient.debug = () => {};
      
      this.setupStompClientHandlers();
      this.stompClient.activate();
    } catch (error) {
      console.error('Error al inicializar WebSocket:', error);
      this.handleReconnection();
    }
  }

  private setupStompClientHandlers() {
    if (!this.stompClient) return;

    this.stompClient.onConnect = (frame) => {
      console.log('Conectado al WebSocket');
      this.connected = true;
      this.connectionState.next(true);
      this.reconnectAttempts = 0;
      
      this.subscribeToPositions();
    };
    
    this.stompClient.onStompError = (frame) => {
      console.error('Error STOMP:', frame);
      this.connectionState.next(false);
      this.handleReconnection();
    };
    
    this.stompClient.onWebSocketError = (event) => {
      console.error('Error de conexión WebSocket:', event);
      this.connected = false;
      this.connectionState.next(false);
      this.handleReconnection();
    };
    
    this.stompClient.onDisconnect = () => {
      console.log('Desconectado del WebSocket');
      this.connected = false;
      this.connectionState.next(false);
      this.handleReconnection();
    };

    this.stompClient.reconnectDelay = 5000;
  }

  isConnected() {
    return this.connectionState.asObservable();
  }

  private subscribeToPositions() {
    this.stompClient?.subscribe('/topic/posiciones-buses', (message) => {
      try {
        const posiciones = JSON.parse(message.body) as GPSDataDTO[];
        console.log('Posiciones recibidas:', posiciones);
        this.posicionesBuses.next(posiciones);
      } catch (e) {
        console.error('Error al procesar mensaje:', e);
      }
    });
  }

  private handleReconnection() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Máximo número de intentos de reconexión alcanzado');
      return;
    }

    this.reconnectAttempts++;
    console.log(`Intento de reconexión ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);

    timer(this.RECONNECT_INTERVAL).subscribe(() => {
      this.initializeConnection();
    });
  }

  connect() {
    if (this.connected) {
      return;
    }
    this.initializeConnection();
  }

  suscribirseABus(busId: number, callback: (posicion: GPSDataDTO) => void) {
    if (this.stompClient && this.stompClient.active) {
      return this.stompClient.subscribe(`/topic/posicion-bus/${busId}`, (message) => {
        try {
          const posicion = JSON.parse(message.body) as GPSDataDTO;
          callback(posicion);
        } catch (e) {
          console.error('Error al procesar mensaje de bus específico:', e);
        }
      });
    } else {
      console.error('No hay conexión establecida al servidor');
      this.connect();
      return null;
    }
  }

  solicitarPosiciones() {
    console.log('Solicitando posiciones de buses...');
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.publish({
        destination: "/app/solicitar-posiciones",
        body: "{}"
      });
    } else {
      console.error('No hay conexión establecida al servidor');
      this.connect();
    }
  }

  getPosicionesObservable() {
    return this.posicionesBuses.asObservable();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
      this.reconnectAttempts = 0;
    }
  }

  ngOnDestroy() {
    this.disconnect();
  }
}