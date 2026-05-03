import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const AS = inject(AuthService);
  const router = inject(Router);

  if (AS.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};