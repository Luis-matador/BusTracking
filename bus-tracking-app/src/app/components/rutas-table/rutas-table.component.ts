import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Ruta } from '../../interfaces/ruta.interface';
import { Parada } from '../../interfaces/parada.interface';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import * as L from 'leaflet';

@Component({
  selector: 'app-rutas-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rutas-table.component.html',
  styleUrls: ['./rutas-table.component.scss']
})
export class RutasTableComponent implements OnInit, OnDestroy, AfterViewInit {
  rutas: Ruta[] = [];
  proximaParada: Parada | null = null;
  loading = true;
  error = '';
  private map: L.Map | null = null;
  private actualizacionSubscription?: Subscription;
  private coloresRuta: { [key: number]: string } = {};
  paradasDestino: { [busId: number]: number } = {};

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.cargarRutas();
    this.iniciarActualizacionParadas();
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.actualizacionSubscription) {
      this.actualizacionSubscription.unsubscribe();
    }
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    this.map = L.map('mapa').setView([37.3886, -5.9823], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);
  }

  private getColorForRuta(rutaId: number): string {
    if (!this.coloresRuta[rutaId]) {
      const colores = [
        '#FF0000', '#00FF00', '#0000FF', '#FFFF00', 
        '#FF00FF', '#00FFFF', '#800000', '#008000', 
        '#000080', '#808000'
      ];
      this.coloresRuta[rutaId] = colores[Object.keys(this.coloresRuta).length % colores.length];
    }
    return this.coloresRuta[rutaId];
  }

  cargarRutas(): void {
    this.apiService.getRutas().subscribe({
      next: (data) => {
        this.rutas = data;
        this.loading = false;
        this.cargarParadasDestino();
      },
      error: (err) => {
        this.error = 'Error al cargar las rutas';
        this.loading = false;
        console.error(err);
      }
    });
  }

  private cargarParadasDestino(): void {
  this.apiService.getBuses().subscribe({
    next: (buses) => {
      buses.forEach(bus => {
        // Obtener la parada destino para cada bus sin verificar estado
        this.actualizarParadaDestino(bus.id);
      });
    },
    error: (err) => console.error('Error al cargar buses:', err)
  });
}

private actualizarParadaDestino(busId: number): void {
  this.apiService.getDestinoBus(busId).subscribe({
    next: (parada) => {
      if (parada) {
        this.paradasDestino[busId] = parada.id;
      }
    },
    error: (err) => console.error(`Error al obtener destino del bus ${busId}:`, err)
  });
}

  private iniciarActualizacionParadas(): void {
    this.actualizacionSubscription = interval(5000)
      .subscribe(() => this.cargarParadasDestino());
  }

  isParadaDestino(parada: Parada): boolean {
    return Object.values(this.paradasDestino).includes(parada.id);
  }
}