import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { provideStore } from '@ngrx/store';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { BarController, BarElement, CategoryScale, Colors, Legend, LinearScale, Title, Tooltip } from 'chart.js';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { provideToastr } from 'ngx-toastr';
import { environment } from '../environments/environment.development';
import { routes } from './app.routes';
import { tokenInterceptor } from './middlewares/token/token.interceptor';
import { AuthReducer } from './store/auth/auth.reducer';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideStore(), // required store providers (ngrx store)
    provideClientHydration(),
    provideStore({
      auth: AuthReducer,
    }), // required store providers (ngrx store)
    provideStoreDevtools({ maxAge: 25, logOnly: environment.production }),
    provideHttpClient(withInterceptors([tokenInterceptor])), // required http interceptor
    provideAnimations(), // required animations providers
    provideToastr({
      positionClass: 'toast-bottom-right',
      progressBar: true,
      progressAnimation: 'decreasing',
      autoDismiss: true,
      preventDuplicates: true,
      timeOut: 3000,
    }), // Toastr providers
    FontAwesomeModule, // FontAwesome module
    provideCharts(withDefaultRegisterables()), // ng2-charts provider with default registerables
    provideCharts({
      registerables: [
        BarController,
        CategoryScale,
        LinearScale,
        BarElement,
        Title,
        Tooltip,
        Legend,
        Colors
      ],
    }), // Additional chart.js registerables
  ],
};
