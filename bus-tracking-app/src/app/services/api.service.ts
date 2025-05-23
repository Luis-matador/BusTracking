import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, map, catchError, throwError, switchMap } from 'rxjs';
import { Ruta } from '../interfaces/ruta.interface';
import { Bus } from '../interfaces/bus.interface';
import { Conductor } from '../interfaces/conductor.interface';
import { Parada } from '../interfaces/parada.interface';
import { Usuario } from '../interfaces/usuario.interface';

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
    return this.http.get<Usuario[]>(`${this.API_URL}/usuarios`).pipe(
      map(usuarios => {
        const usuario = usuarios.find(u => u.email === email);
        if (!usuario) {
          throw new Error('Usuario no encontrado');
        }
        return usuario;
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error buscando usuario:', error);
        return throwError(() => new Error('Error en el servidor'));
      })
    );
  }

  crearUsuario(usuario: Usuario): Observable<Usuario> {

    return this.http.post<Usuario>(`${this.API_URL}/usuarios`, usuario).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error creando usuario:', error);
        return throwError(() => new Error('Error al crear usuario'));
      })
    );
  }

  login(email: string, password: string): Observable<Usuario> {
    return this.http.get<Usuario[]>(`${this.API_URL}/usuarios`).pipe(
      map(usuarios => {
        const usuario = usuarios.find(u => u.email === email && u.password === password);
        if (!usuario) {
          throw new Error('Email o contraseña incorrectos');
        }
        return usuario;
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error en login:', error);
        return throwError(() => new Error('Error al iniciar sesión'));
      })
    );
  }

  register(usuario: Usuario): Observable<Usuario> {
    return this.http.get<Usuario[]>(`${this.API_URL}/usuarios`).pipe(
      switchMap(usuarios => {
        const existingUser = usuarios.find(u => u.email === usuario.email);
        if (existingUser) {
          throw new Error('El email ya está registrado');
        }
        return this.crearUsuario(usuario);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error en registro:', error);
        if (error.message === 'El email ya está registrado') {
          return throwError(() => error);
        }
        return throwError(() => new Error('Error al registrar usuario'));
      })
    );
  }

  
}