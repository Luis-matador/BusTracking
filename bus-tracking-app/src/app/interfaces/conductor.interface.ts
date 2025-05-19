import { Bus } from './bus.interface';

export interface Conductor {
    id: number;
    nombre: string;
    apellido: string;
    dni: string;
    tel: string;
    busAsignado?: Bus;
}