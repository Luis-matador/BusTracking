import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { importProvidersFrom } from '@angular/core';
import { GoogleMapsModule } from '@angular/google-maps'; // Importamos el módulo de Google Maps

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    importProvidersFrom(GoogleMapsModule) // 👈 Importamos y registramos Google Maps
  ]
};