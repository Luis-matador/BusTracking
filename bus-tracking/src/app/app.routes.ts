import { Routes } from '@angular/router';
import { MapComponent } from './map/map.component'; // Asegúrate de que la ruta sea correcta

export const routes: Routes = [
  {
    path: '', // Ruta raíz
    component: MapComponent, // Carga MapComponent para la raíz
  },
  {
    path: '**', // Para cualquier ruta no encontrada
    redirectTo: '', // Redirige a la raíz (puedes cambiarla si deseas otra ruta por defecto)
  },
];