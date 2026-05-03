import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-template',
  templateUrl: './template.component.html',
  styleUrls: ['./template.component.css']
})
export class TemplateComponent {

  constructor(private AS: AuthService, private router: Router) {}

  isAdmin(): boolean {
    return this.AS.isAdmin();
  }

  isSeller(): boolean {
    return this.AS.isSeller();
  }

  isCustomer(): boolean {
    return this.AS.isCustomer();
  }

  getRole(): string {
    return this.AS.getRole() || '';
  }

  logout() {
    this.AS.logout();
    this.router.navigate(['/login']);
  }
}
