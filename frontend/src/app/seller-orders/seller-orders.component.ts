import { AfterViewInit, Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { OrderService } from '../services/order.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-seller-orders',
  templateUrl: './seller-orders.component.html',
  styleUrls: ['./seller-orders.component.css']
})
export class SellerOrdersComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  dataSource = new MatTableDataSource<any>();
  displayedColumns = ['numeroCommande', 'dateCommande', 'adresse', 'produits', 'total', 'statut'];

  lastRefresh: Date = new Date();
  private refreshSub!: Subscription;

  constructor(private OS: OrderService) {}

  ngOnInit() {
    this.loadOrders();
    // Auto-refresh toutes les 30 secondes
    this.refreshSub = interval(30000).subscribe(() => this.loadOrders());
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy() {
    if (this.refreshSub) this.refreshSub.unsubscribe();
  }

  loadOrders() {
    this.OS.GetSellerOrders().subscribe(res => {
      this.dataSource.data = res;
      this.lastRefresh = new Date();
    });
  }

  applyFilter(event: Event) {
    const val = (event.target as HTMLInputElement).value;
    this.dataSource.filter = val.trim().toLowerCase();
    if (this.dataSource.paginator) this.dataSource.paginator.firstPage();
  }
}
