import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ruta } from '../interfaces/ruta.interface';
import { Bus } from '../interfaces/bus.interface';
import { Conductor } from '../interfaces/conductor.interface';
import { Parada } from '../interfaces/parada.interface';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getRutas(): Observable<Ruta[]> {
    return this.http.get<Ruta[]>(`${this.API_URL}/rutas`);
  }

  getBuses(): Observable<Bus[]> {
    return this.http.get<Bus[]>(`${this.API_URL}/buses`);
  }

  getConductores(): Observable<Conductor[]> {
    return this.http.get<Conductor[]>(`${this.API_URL}/conductores`);
  }

  getDestinoBus(busId: number): Observable<Parada> {
    return this.http.get<Parada>(`${this.API_URL}/buses/${busId}/destino`);
  }
}