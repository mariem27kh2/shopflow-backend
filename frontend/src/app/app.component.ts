import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  currentUrl: string = '/';

  constructor(public AS: AuthService, private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.currentUrl = event.urlAfterRedirects;
      }
    });
  }

  // Page login seule → aucun layout
  isLoginPage(): boolean {
    return this.currentUrl === '/login'
      || this.currentUrl === '/register';
  }

  // Sidebar admin/seller → dashboard, products, categories, admin-orders
  isSidebarLayout(): boolean {
    if (this.isLoginPage()) return false;
    return this.currentUrl.startsWith('/dashboard')
      || this.currentUrl.startsWith('/products')
      || this.currentUrl.startsWith('/categories')
      || this.currentUrl.startsWith('/admin-orders')
      || this.currentUrl.startsWith('/seller-orders')
      || this.currentUrl.startsWith('/reviews-pending');
  }

  // Navbar publique → tout le reste (shop, cart, orders, checkout, product/:id)
  isNavbarLayout(): boolean {
    return !this.isLoginPage() && !this.isSidebarLayout();
  }
}
