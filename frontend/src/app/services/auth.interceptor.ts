import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private AS: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Routes GET publiques — pas de token
    const isPublicGet = req.method === 'GET' && (
      req.url.includes('/api/products') ||
      req.url.includes('/api/reviews/product')
    );

    // Auth routes — jamais de token
    const isAuthRoute = req.url.includes('/api/auth/');

    if (isPublicGet || isAuthRoute) {
      return next.handle(req);
    }

    const token = this.AS.getToken();

    if (token) {
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token.trim()}` 
        }
      });
      return next.handle(cloned);
    }

    return next.handle(req);
  }
}