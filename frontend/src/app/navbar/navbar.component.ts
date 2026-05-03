import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  constructor(public AS: AuthService, private router: Router) {}

  isLoggedIn(): boolean { return this.AS.isLoggedIn(); }
  isCustomer(): boolean { return this.AS.isCustomer(); }

  logout() {
    this.AS.logout();
    this.router.navigate(['/shop']);
  }

  goLogin() {
    this.router.navigate(['/login']);
  }
}
