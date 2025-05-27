import { Routes } from '@angular/router';

// Importa tus componentes aquí
import { HomeComponent } from './home/home.component';
import { AvatarComponent } from './avatar/avatar.component';

// Si ya creaste los componentes para estas rutas, impórtalos también
// import { ProgramasComponent } from './programas/programas.component';
// import { AlumnosComponent } from './alumnos/alumnos.component';
// import { WhatsappComponent } from './whatsapp/whatsapp.component';
// import { NosotrosComponent } from './nosotros/nosotros.component';

export const routes: Routes = [
  // Ruta por defecto: redirige la URL vacía a /home
  { path: '', redirectTo: '/home', pathMatch: 'full' },

  // Ruta para el componente Home (tu antigua index.html)
  { path: 'home', component: HomeComponent },

  // Ruta para el componente Avatar (tu antigua avatar.html)
  { path: 'avatar', component: AvatarComponent },

  // **IMPORTANTE:** Si creaste los componentes para "programas", "Alumnos", etc.,
  // debes descomentar y añadir sus rutas aquí.
  // Ejemplo:
  // { path: 'programas', component: ProgramasComponent },
  // { path: 'alumnos', component: AlumnosComponent },
  // { path: 'whatsapp', component: WhatsappComponent },
  // { path: 'nosotros', component: NosotrosComponent },

  // Ruta comodín: si ninguna de las rutas anteriores coincide, redirige a /home
  { path: '**', redirectTo: '/home' }
];