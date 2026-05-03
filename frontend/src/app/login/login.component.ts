import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email: string = '';
  motDePasse: string = '';
  loading: boolean = false;
  errorMsg: string = '';

  constructor(private AS: AuthService, private router: Router) {}

  login() {
    if (this.loading) return;
    if (!this.email || !this.motDePasse) {
      this.errorMsg = 'Veuillez remplir tous les champs';
      return;
    }

    this.loading = true;
    this.errorMsg = '';

    this.AS.login({ email: this.email, motDePasse: this.motDePasse }).subscribe({
      next: (res) => {
        this.AS.saveToken(res.accessToken);
        this.AS.saveRole(res.role);

        if (res.role === 'ADMIN' || res.role === 'SELLER') {
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/shop']);
        }
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Email ou mot de passe incorrect';
      }
    });
  }
}
