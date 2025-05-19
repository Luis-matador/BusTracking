import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapComponent } from './map/map.component';
import { RutasTableComponent } from './components/rutas-table/rutas-table.component';
import { BusesTableComponent } from './components/buses-table/buses-table.component';
import { ConductoresTableComponent } from './components/conductores-table/conductores-table.component';
import { WebsocketService } from './services/websocket.service';
import { Subscription } from 'rxjs';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, MapComponent, RutasTableComponent, BusesTableComponent, ConductoresTableComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mapa-sevilla';
  menuVisible = false;
  vistaActual: string | null = null;

  private wsSubscription: Subscription | null = null;

  constructor(private websocketService: WebsocketService) {}

  ngOnInit() {
    // Iniciar conexión WebSocket al cargar la aplicación
    this.websocketService.connect();
    
    // Supervisar el estado de la conexión
    this.wsSubscription = this.websocketService.isConnected().subscribe(
      connected => {
        if (!connected) {
          console.log('Reconectando WebSocket desde AppComponent...');
          this.websocketService.connect();
        }
      }
    );
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }

  toggleMenu() {
    this.menuVisible = !this.menuVisible;
  }

  volverInicio() {
    this.vistaActual = null;
  }

  mostrarRutas() {
    this.vistaActual = 'rutas';
    this.menuVisible = false;
  }

  mostrarBuses() {
  this.vistaActual = 'buses';
  this.menuVisible = false;
}

mostrarConductores() {
    this.vistaActual = 'conductores';
    this.menuVisible = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const menuContainer = document.querySelector('.menu-container');
    const menuButton = document.querySelector('.menu-button');
    
    if (!menuContainer?.contains(event.target as Node) && 
        !menuButton?.contains(event.target as Node)) {
      this.menuVisible = false;
    }
  }
}