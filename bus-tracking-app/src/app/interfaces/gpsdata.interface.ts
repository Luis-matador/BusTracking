import { Bus } from './bus.interface';

export interface GPSData {
    id: number;
    latitud: number;
    longitud: number;
    tiempo: string;
    velocidad: number;
    direccion: number;
    bus?: Bus;
}