import { Parada } from './parada.interface';
import { Bus } from './bus.interface';

export interface Ruta {
    id: number;
    nombre: string;
    info: string;
    paradas: Parada[];
    buses: Bus[];
}