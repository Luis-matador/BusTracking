import { Ruta } from './ruta.interface';

export interface Parada {
    id: number;
    nombre: string;
    latitud: number;
    longitud: number;
    orden: number;
    rutaAsociada?: Ruta;
}