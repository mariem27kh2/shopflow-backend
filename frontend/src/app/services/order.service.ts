import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private apiUrl = 'http://localhost:8081/api/orders';

  constructor(private http: HttpClient) {}

  CreateOrder(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  GetMyOrders(): Observable<any[]> {
    return this.http.get<any>(`${this.apiUrl}/my?page=0&size=100`).pipe(
      map(res => res.content ? res.content : res)
    );
  }

  GetOrderById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
GetAllOrders(): Observable<any[]> {
  return this.http.get<any>(`${this.apiUrl}?page=0&size=100`).pipe(
    map(res => res.content ? res.content : res)
  );
}

GetSellerOrders(): Observable<any[]> {
  return this.http.get<any>(`${this.apiUrl}/seller?page=0&size=100`).pipe(
    map(res => res.content ? res.content : res)
  );
}

UpdateStatus(id: number, statut: string): Observable<any> {
  return this.http.put<any>(`${this.apiUrl}/${id}/status?statut=${statut}`, {});
}
  CancelOrder(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/cancel`, {});
  }
}