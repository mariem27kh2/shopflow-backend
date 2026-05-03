import { Component, OnInit, OnDestroy } from '@angular/core';
import { OrderService } from '../services/order.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-admin-orders',
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit, OnDestroy {

  orders: any[] = [];
  lastRefresh: Date = new Date();
  private refreshSub!: Subscription;

  statuts: string[] = [
    'PENDING', 'PAID', 'PROCESSING', 
    'DELIVERED', 'CANCELLED'
  ];

  constructor(private OS: OrderService) {}

  ngOnInit() {
    this.loadOrders();
    this.refreshSub = interval(30000).subscribe(() => this.loadOrders());
  }

  ngOnDestroy() {
    if (this.refreshSub) this.refreshSub.unsubscribe();
  }

  loadOrders() {
    this.OS.GetAllOrders().subscribe(res => {
      this.orders = res;
      this.lastRefresh = new Date();
    });
  }

  changeStatus(id: number, statut: string) {
    this.OS.UpdateStatus(id, statut).subscribe(() => {
      this.loadOrders();
    });
  }
}
