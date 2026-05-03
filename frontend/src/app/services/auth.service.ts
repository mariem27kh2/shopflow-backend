import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginRequest, AuthResponse } from '../Models/auth.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8081/api/auth';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, data);
  }

  saveToken(token: string) {
    localStorage.setItem('accessToken', token.trim()); 
  }

  saveRole(role: string) {
    localStorage.setItem('role', role.trim()); 
  }

  getToken() {
    return localStorage.getItem('accessToken');
  }

  getRole() {
    return localStorage.getItem('role');
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isSeller(): boolean {
    return this.getRole() === 'SELLER';
  }

  isCustomer(): boolean {
    return this.getRole() === 'CUSTOMER';
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.clear();
  }
}