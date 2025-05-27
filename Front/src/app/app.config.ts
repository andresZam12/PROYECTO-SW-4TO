// src/app/app.config.ts (VERIFICA ESTO, NO LO CAMBIES SI YA ESTÁ ASÍ)

import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router'; // Asegúrate de que provideRouter esté aquí

import { routes } from './app.routes'; // <-- IMPORTANTE: Esta línea debe estar
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes), // <-- IMPORTANTE: Esta línea debe estar
    provideClientHydration(withEventReplay())
  ]
};