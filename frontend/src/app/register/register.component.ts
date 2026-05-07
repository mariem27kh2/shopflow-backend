import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../services/api.config';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  prenom: string = '';
  nom: string = '';
  email: string = '';
  motDePasse: string = '';
  confirmMotDePasse: string = '';

  loading: boolean = false;
  errorMsg: string = '';
  successMsg: string = '';

  constructor(
    private http: HttpClient,
    private AS: AuthService,
    private router: Router
  ) {}

  register() {
    this.errorMsg = '';

    if (!this.prenom || !this.nom || !this.email || !this.motDePasse) {
      this.errorMsg = 'Veuillez remplir tous les champs';
      return;
    }

    if (this.motDePasse.length < 6) {
      this.errorMsg = 'Le mot de passe doit contenir au moins 6 caractères';
      return;
    }

    if (this.motDePasse !== this.confirmMotDePasse) {
      this.errorMsg = 'Les mots de passe ne correspondent pas';
      return;
    }

    this.loading = true;

    const data = {
      prenom: this.prenom,
      nom: this.nom,
      email: this.email,
      motDePasse: this.motDePasse,
      role: 'CUSTOMER'
    };

    this.http.post<any>(`${API_BASE_URL}/api/auth/register`, data).subscribe({
      next: (res) => {
        // Connexion automatique après inscription
        this.AS.saveToken(res.accessToken);
        this.AS.saveRole(res.role);
        this.router.navigate(['/shop']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err.error?.message || 'Erreur lors de l\'inscription';
      }
    });
  }
}
