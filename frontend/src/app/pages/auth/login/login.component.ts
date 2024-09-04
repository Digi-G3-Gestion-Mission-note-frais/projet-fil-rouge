import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { Store } from '@ngrx/store';
import ms from 'ms';
import { CookieService } from 'ngx-cookie-service';
import { ToastrService } from 'ngx-toastr';
import { CustomButtonComponent } from '../../../components/buttons/custom-button.component';
import { AuthService } from '../../../services/auth/auth.service';
import { loginAction } from '../../../store/auth/auth.actions';
import { AuthStateReducer } from '../../../store/auth/auth.reducer';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    CustomButtonComponent,
  ],
  providers: [RouterLink, Router, CookieService, ToastrService],
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  loginForm = new FormGroup({
    email: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  faEyeIcon = faEye;
  faEyeSlashIcon = faEyeSlash;
  showPassword: boolean = false;
  bgImage: string =
    'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?q=80&w=2084&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';

  constructor(
    private authService: AuthService,
    private cookie: CookieService,
    private router: Router,
    private toastr: ToastrService,
    private store: Store<AuthStateReducer>
  ) {}

  ngOnInit() {
    if (this.cookie.check('jwt_token')) this.router.navigate(['/']);
  }

  submitLogin() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.getRawValue()).subscribe((data) => {
        const today = new Date(Date.now() + ms('30m'));
        this.cookie.set('jwt_token', data, {
          expires: today,
        });

        this.authService.currentUser().subscribe((data) => {
          this.store.dispatch(
            loginAction({
              user: data,
            })
          );
          this.router.navigate(['/']);
        });
      });
    } else {
      this.toastr.error('Veuillez renseigner tous les champs', 'Erreur');
    }
  }
}
