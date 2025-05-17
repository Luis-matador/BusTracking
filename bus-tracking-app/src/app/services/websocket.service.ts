// src/app/services/websocket.service.ts
import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import { GPSDataDTO } from '../models/gps-data-dto.model';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stompClient: Client | null = null;
  private posicionesBuses = new BehaviorSubject<GPSDataDTO[]>([]);
  private connected = false;

  constructor() { }

  connect() {
    if (this.connected) {
      return; // Ya está conectado
    }

    console.log('Iniciando conexión WebSocket...');
    try {
      const socket = new SockJS('http://localhost:8080/ws-buses');
      this.stompClient = new Client();
      
      this.stompClient.webSocketFactory = () => {
        return socket as any;
      };
      
      // Desactivar logs de debug
      this.stompClient.debug = () => {};
      
      // Manejador de conexión exitosa
      this.stompClient.onConnect = (frame) => {
        console.log('Conectado al WebSocket');
        this.connected = true;
        
        this.stompClient?.subscribe('/topic/posiciones-buses', (message) => {
          try {
            const posiciones = JSON.parse(message.body) as GPSDataDTO[];
            console.log('Posiciones recibidas:', posiciones);
            this.posicionesBuses.next(posiciones);
          } catch (e) {
            console.error('Error al procesar mensaje:', e);
          }
        });
      };
      
      // Manejador de errores
      this.stompClient.onStompError = (frame) => {
        console.error('Error STOMP:', frame);
      };
      
      // Manejador de errores de WebSocket
      this.stompClient.onWebSocketError = (event) => {
        console.error('Error de conexión WebSocket:', event);
        this.connected = false;
        // Reintentar conexión después de 5 segundos
        setTimeout(() => {
          this.connect();
        }, 5000);
      };
      
      // Manejador de desconexión
      this.stompClient.onDisconnect = () => {
        console.log('Desconectado del WebSocket');
        this.connected = false;
      };
      
      // Configuración de reconexión
      this.stompClient.reconnectDelay = 5000;
      
      // Activar la conexión
      this.stompClient.activate();
    } catch (error) {
      console.error('Error al inicializar WebSocket:', error);
    }
  }

  // Método para suscribirse a un bus específico
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
      this.connect(); // Intentar conectar si no está conectado
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
      this.connect(); // Intentar conectar si no está conectado
    }
  }

  getPosicionesObservable() {
    return this.posicionesBuses.asObservable();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
    }
  }
}