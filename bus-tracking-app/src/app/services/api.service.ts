import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Ruta } from '../interfaces/ruta.interface';
import { Bus } from '../interfaces/bus.interface';
import { Conductor } from '../interfaces/conductor.interface';
import { Parada } from '../interfaces/parada.interface';
import { Usuario, RegistroRequest } from '../interfaces/usuario.interface';

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

  actualizarUsuario(id: number, usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.API_URL}/usuarios/${id}`, usuario);
  }

  borrarUsuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/usuarios/${id}`);
  }

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.API_URL}/usuarios`);
  }

  buscarUsuarioEmail(email: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.API_URL}/usuarios/email/${encodeURIComponent(email)}`);
  }

  login(email: string, password: string): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.API_URL}/auth/login`, { email, password }).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.mensajeError(error, 'Error al iniciar sesión'))))
    );
  }

  register(datos: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.API_URL}/auth/registro`, datos).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.mensajeError(error, 'Error al registrar usuario'))))
    );
  }

  /** Usa el mensaje que devuelve el backend ({ error: '...' }) si existe. */
  private mensajeError(error: HttpErrorResponse, porDefecto: string): string {
    return error.error?.error ?? porDefecto;
  }
}
