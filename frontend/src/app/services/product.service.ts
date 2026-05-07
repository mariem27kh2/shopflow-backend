import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product, ProductRequest } from '../Models/product.model';
import { Observable, map } from 'rxjs';
import { API_BASE_URL } from './api.config';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = `${API_BASE_URL}/api/products`;

  constructor(private http: HttpClient) {}

  GetAllProducts(): Observable<Product[]> {
    return this.http.get<any>(`${this.apiUrl}?page=0&size=100`).pipe(
      map(res => res.content ? res.content : res)
    );
  }

  GetProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  AddProduct(product: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  UpdateProduct(id: number, product: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  DeleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}