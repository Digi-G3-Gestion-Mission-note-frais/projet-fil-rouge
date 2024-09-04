import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterModule
} from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faBook,
  faCoins,
  faGear,
  faHouse,
  faRightFromBracket,
  faScroll,
  faSuitcase,
  faUsers,
  IconDefinition,
} from '@fortawesome/free-solid-svg-icons';
import { Store } from '@ngrx/store';
import ms from 'ms';
import { Observable } from 'rxjs';
import { SidebarMenuType, UserType } from '../interfaces/types';
import { AuthService } from '../services/auth/auth.service';
import { loginAction } from '../store/auth/auth.actions';
import { AuthStateReducer } from '../store/auth/auth.reducer';
import { authSelector } from '../store/auth/auth.selectors';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [FontAwesomeModule, CommonModule, RouterModule],
  providers: [AuthService, Router, CookieService, RouterLink],
  templateUrl: './layout.component.html',
})
export class LayoutComponent implements OnInit {
  // FontAwesome icons
  faHouseIcon: IconDefinition = faHouse;
  faUsersIcon: IconDefinition = faUsers;
  faBookIcon: IconDefinition = faBook;
  faSuitcaseIcon: IconDefinition = faSuitcase;
  faCoinsIcon: IconDefinition = faCoins;
  faScrollIcon: IconDefinition = faScroll;
  faGearIcon: IconDefinition = faGear;
  faLogoutIcon: IconDefinition = faRightFromBracket;

  // Sidebar Menu
  sidebarMenu: SidebarMenuType = {
    top: [
      {
        name: 'Tableau de bord',
        link: '',
        icon: this.faHouseIcon,
        active: false,
      },
      {
        icon: this.faUsersIcon,
        link: '/collaborators',
        name: 'Collaborateurs',
        active: false,
      },
      {
        icon: this.faBookIcon,
        link: '/missions',
        name: 'Missions',
        active: false,
      },
      {
        icon: this.faSuitcaseIcon,
        link: '/naturemissions',
        name: 'Nature des missions',
        active: false,
      },
      {
        icon: this.faScrollIcon,
        link: '/expenses',
        name: 'Notes de frais',
        active: false,
      },
    ],
    bottom: [
      {
        icon: this.faGearIcon,
        link: '/settings/user-details',
        name: 'Paramètres',
        active: false,
      },
      {
        icon: this.faLogoutIcon,
        link: '/logout',
        name: 'Deconnexion',
        active: false,
      },
    ],
  };

  // Active page
  activePage: string = '';

  // User Status
  userStatus: boolean = false;
  user$: Observable<UserType | null> | null = null;

  /**
   * constructor
   */
  constructor(
    private router: Router,
    private cookie: CookieService,
    private authService: AuthService,
    private store: Store<AuthStateReducer>
  ) {}

  ngOnInit() {
    // Redirection to Login page if user is not connected
    this.userStatus = this.cookie.check('jwt_token');
    if (!this.userStatus) this.router.navigateByUrl('/login');
    else {
      this.refreshToken();
      this.fetchCurrentUser();
      this.getCurrentUser();
    }

    // Get the active page
    this.activePage = this.router.url.replace('/', '').split('/')[0];

    // Set active page
    for (const item of this.sidebarMenu.top) {
      if (item.link === this.activePage) {
        item.active = true;
      }
    }

    for (const item of this.sidebarMenu.bottom) {
      if (item.link.replace('/', '').split('/')[0] === this.activePage) {
        item.active = true;
      }
    }
  }

  /**  Logout the user
   *
   * Delete the cookie and redirect to the login page
   */
  async logout() {
    this.cookie.delete('jwt_token');
    await this.router.navigate(['/login']);
  }

  /** Refresh the token
   * Call the API to refresh the token
   */
  refreshToken() {
    // Call the API to refresh the token
    this.authService.refresh().subscribe((data: string) => {
      const today = new Date(Date.now() + ms('30m'));
      this.cookie.set('jwt_token', data, { expires: today });
    });
  }

  /** Get the current user
   * Call the API to get the current user
   */
  fetchCurrentUser() {
    this.authService.currentUser().subscribe((user) => {
      this.store.dispatch(loginAction({ user }));
    });
  }

  getCurrentUser() {
    this.user$ = this.store.select(authSelector);
  }
}
