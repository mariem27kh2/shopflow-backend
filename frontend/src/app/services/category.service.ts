import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Category } from '../Models/category.model';
import { Observable, catchError, of } from 'rxjs';
import { API_BASE_URL } from './api.config';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private apiUrl = `${API_BASE_URL}/api/categories`;

  constructor(private http: HttpClient) {}

  GetAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('Erreur chargement catégories', err);
        return of([]);
      })
    );
  }

  AddCategory(category: any): Observable<any> {
    // ✅ Tout dans le body JSON
    const body: any = {
      nom: category.nom,
      description: category.description
    };

    if (category.parentId) {
      body.parentId = category.parentId; // ✅ parentId dans le body
    }

    return this.http.post(this.apiUrl, body).pipe(
      catchError(err => {
        console.error('Erreur ajout catégorie', err);
        return of(null);
      })
    );
  }

  UpdateCategory(id: number, category: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, category).pipe(
      catchError(err => {
        console.error('Erreur modification catégorie', err);
        return of(null);
      })
    );
  }

  DeleteCategory(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error('Erreur suppression catégorie', err);
        return of(null);
      })
    );
  }
}