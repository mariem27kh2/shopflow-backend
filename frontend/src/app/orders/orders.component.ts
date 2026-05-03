import { Component, OnInit } from '@angular/core';
import { OrderService } from '../services/order.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {

  orders: any[] = [];

  constructor(private OS: OrderService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.OS.GetMyOrders().subscribe(res => {
      this.orders = res;
    });
  }

  cancelOrder(id: number) {
    if (confirm('Voulez-vous annuler cette commande ?')) {
      this.OS.CancelOrder(id).subscribe(() => {
        this.loadOrders();
      });
    }
  }
}