import { Ruta } from './ruta.interface';
import { Conductor } from './conductor.interface';
import { GPSData } from './gpsdata.interface';

export interface Bus {
    id: number;
    marca: string;
    modelo: string;
    capacidad: number;
    matricula: string;
    conductor?: Conductor;
    ruta?: Ruta;
    datosGPS: GPSData[];
}