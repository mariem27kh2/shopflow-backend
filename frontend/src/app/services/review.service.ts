import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';

export interface Review {
  id: number;
  customerNom: string;
  note: number;
  commentaire: string;
  dateCreation: string;
  approuve: boolean;
}

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = `${API_BASE_URL}/api/reviews`;

  constructor(private http: HttpClient) {}

  GetProductReviews(productId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/product/${productId}`);
  }

  GetPendingReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/pending`);
  }

  ApproveReview(id: number): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${id}/approve`, {});
  }

  CreateReview(data: { productId: number; note: number; commentaire: string }): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, data);
  }
}
