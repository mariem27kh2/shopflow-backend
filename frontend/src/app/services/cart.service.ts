import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cart } from '../Models/cart.model';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = `${API_BASE_URL}/api/cart`;

  constructor(private http: HttpClient) {}

  GetCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl);
  }

  AddItem(productId: number, quantite: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/items`, {
      productId: productId,
      quantite: quantite
    });
  }

  UpdateItem(itemId: number, quantite: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/items/${itemId}?quantite=${quantite}`, {});
  }

  RemoveItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${itemId}`);
  }
}