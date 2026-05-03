import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const adminSellerGuard: CanActivateFn = () => {
  const AS = inject(AuthService);
  const router = inject(Router);

  if (AS.isAdmin() || AS.isSeller()) {
    return true;
  }

  // Pas connecté → login
  // Connecté mais customer → boutique
  if (AS.isLoggedIn()) {
    router.navigate(['/shop']);
  } else {
    router.navigate(['/login']);
  }
  return false;
};
