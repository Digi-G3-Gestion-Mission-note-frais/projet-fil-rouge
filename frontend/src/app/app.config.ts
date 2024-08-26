import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideStore } from '@ngrx/store';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { BarController, Legend, Colors, BarElement, CategoryScale, LinearScale, Title, Tooltip } from 'chart.js';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes), provideStore(), provideClientHydration(), provideHttpClient(), provideAnimationsAsync(), provideCharts(withDefaultRegisterables()), provideCharts(withDefaultRegisterables()),
    provideCharts({
      registerables: [
        BarController, 
        CategoryScale, 
        LinearScale, 
        BarElement, 
        Title, 
        Tooltip, 
        Legend
      ]
    })]
};
