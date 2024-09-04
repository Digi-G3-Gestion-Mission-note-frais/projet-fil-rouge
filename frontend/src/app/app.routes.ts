// app.routes.ts
import { Routes } from '@angular/router';
import { AuthGuardService } from './middlewares/auth/auth-guard.service';
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { CollaboratorsHomeComponent } from './pages/collaborators/home/collaborators-home.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { HomeComponent } from './pages/home/home.component';
import { BountiesViewComponent } from './pages/missions/bounties-view/bounties-view.component';
import { MissionDetailsComponent } from './pages/missions/mission-details/mission-details.component';
import { MissionsListComponent } from './pages/missions/missions-list/missions-list.component';
import { NewMissionViewComponent } from './pages/missions/new-mission-view/new-mission-view.component';
import { AdminMissionListComponent } from './pages/nature-mission/admin-mission-list/admin-mission-list.component';
import { NatureMissionCreateComponent } from './pages/nature-mission/nature-mission-create/nature-mission-create.component';
import { DeleteNatureMissionComponent } from './pages/nature-mission/nature-mission-delete/delete-nature-mission.component';
import { NatureMissionEditComponent } from './pages/nature-mission/nature-mission-edit/nature-mission-edit.component';
import { UserChangePasswordComponent } from './pages/settings/user-change-password/user-change-password.component';
import { UserDetailsComponent } from './pages/settings/user-details/user-details.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'naturemissions', component: AdminMissionListComponent },
  { path: 'naturemissions/create', component: NatureMissionCreateComponent },
  { path: 'naturemissions/delete', component: DeleteNatureMissionComponent },
  { path: 'naturemissions/edit/:id', component: NatureMissionEditComponent },
  // Autres routes ici
  { path: '', redirectTo: '/naturemissions', pathMatch: 'full' },
  { path: '**', redirectTo: '/naturemissions' },



  // { path: 'dashboard', component: DashboardComponent },
  //  { path: 'missions', component: MissionsComponent },
  {
    path: '',
    component: DashboardComponent,
    pathMatch: 'full',
    canActivate: [AuthGuardService],
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'collaborators',
    component: CollaboratorsHomeComponent,
  },
  {
    path: 'settings',
    children: [
      {
        path: 'user-details',
        component: UserDetailsComponent,
      },
      {
        path: 'change-password',
        component: UserChangePasswordComponent,
      },
    ],
  },
  {
    path: 'missions',
    component:MissionsListComponent,
  },
  {
    path: 'missions/new',
    component:NewMissionViewComponent,
  },
  {
    path: 'missions/bounties',
    component:BountiesViewComponent,
  },
  {
    path: 'missions/:id',
    component:MissionDetailsComponent,
  },
];
