import { AfterViewInit, Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ChartDataset, ChartOptions } from 'chart.js';
import { interval, Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../services/api.config';

import { ProductService } from '../services/product.service';
import { CategoryService } from '../services/category.service';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { ProductFormComponent } from '../product-form/product-form.component';
import { Product } from '../Models/product.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  isAdmin = false;
  isSeller = false;

  // Stats admin
  nbProducts: number = 0;
  nbCategories: number = 0;
  nbOrders: number = 0;
  nbSellers: number = 0;

  // Stats seller
  nbMesProduits: number = 0;
  nbMesCommandes: number = 0;
  nbStockFaible: number = 0;
  mesRevenus: number = 0;

  lastRefresh: Date = new Date();
  private refreshSub!: Subscription;

  chartData: ChartDataset[] = [{ data: [], label: 'Stock', backgroundColor: '#7c3aed', borderRadius: 6 } as any];
  chartLabels: string[] = [];
  chartDataPie: ChartDataset[] = [{ data: [], backgroundColor: ['#22c55e', '#f87171'] }];
  chartLabelsPie: string[] = ['Actifs', 'Inactifs'];
  chartDataDoughnut: ChartDataset[] = [{ data: [], backgroundColor: ['#7c3aed', '#e0e7ff'] }];
  chartLabelsDoughnut: string[] = ['Avec promo', 'Sans promo'];
  chartDataPie2: ChartDataset[] = [{ data: [], backgroundColor: ['#3b82f6', '#f59e0b', '#ec4899', '#22c55e', '#8b5cf6', '#06b6d4'] }];
  chartLabelsPie2: string[] = [];

  chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top', labels: { font: { size: 12 }, color: '#374151' } }
    }
  };

  dataSource = new MatTableDataSource<Product>();
  displayedColumns: string[] = ['image', 'id', 'nom', 'prix', 'prixPromo', 'stock', 'actif', 'categories', 'actions'];

  constructor(
    private PS: ProductService,
    private CS: CategoryService,
    private OS: OrderService,
    private AS: AuthService,
    private dialog: MatDialog,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.isAdmin = this.AS.isAdmin();
    this.isSeller = this.AS.isSeller();
    this.loadAll();
    this.refreshSub = interval(30000).subscribe(() => this.loadAll());
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy() {
    if (this.refreshSub) this.refreshSub.unsubscribe();
  }

  loadAll() {
    if (this.isAdmin) {
      this.loadAdminDashboard();
    } else {
      this.loadSellerDashboard();
    }
  }

  // ── Dashboard ADMIN ──────────────────────────────────────────
  loadAdminDashboard() {
    forkJoin({
      products: this.PS.GetAllProducts(),
      categories: this.CS.GetAllCategories(),
      orders: this.OS.GetAllOrders(),
      stats: this.http.get<any>(`${API_BASE_URL}/api/dashboard/stats`)
        .pipe(catchError(() => of({ nbSellers: 0 })))
    }).subscribe(({ products, categories, orders, stats }) => {
      this.nbProducts = products.length;
      this.nbCategories = categories.length;
      this.nbOrders = orders.length;
      this.nbSellers = stats.nbSellers || 0;
      this.dataSource.data = products;
      this.lastRefresh = new Date();
      this.buildCharts(products);
    });
  }

  // ── Dashboard SELLER ─────────────────────────────────────────
  loadSellerDashboard() {
    forkJoin({
      products: this.PS.GetAllProducts(),
      orders: this.OS.GetSellerOrders().pipe(catchError(() => of([])))
    }).subscribe(({ products, orders }) => {
      this.nbMesProduits = products.length;
      this.nbMesCommandes = (orders as any[]).length;
      this.nbStockFaible = products.filter((p: any) => p.stock < 5).length;
      this.mesRevenus = (orders as any[]).reduce((sum: number, o: any) => sum + (o.sousTotal || 0), 0);
      this.dataSource.data = products;
      this.lastRefresh = new Date();
      this.buildCharts(products);
    });
  }

  buildCharts(products: Product[]) {
    let actifs = 0, inactifs = 0, promo = 0, sansPromo = 0;
    products.forEach(p => {
      if (p.actif) actifs++; else inactifs++;
      if (p.prixPromo) promo++; else sansPromo++;
    });

    this.chartLabels = products.map(p => p.nom);
    this.chartData = [{ data: products.map(p => p.stock), label: 'Stock', backgroundColor: '#7c3aed', borderRadius: 6 } as any];
    this.chartDataPie = [{ data: [actifs, inactifs], backgroundColor: ['#22c55e', '#f87171'] }];
    this.chartDataDoughnut = [{ data: [promo, sansPromo], backgroundColor: ['#7c3aed', '#e0e7ff'] }];

    const catCount: { [cat: string]: number } = {};
    products.forEach(p => (p.categories || []).forEach(cat => {
      catCount[cat] = (catCount[cat] || 0) + 1;
    }));
    this.chartLabelsPie2 = Object.keys(catCount);
    this.chartDataPie2 = [{ data: Object.values(catCount), backgroundColor: ['#3b82f6', '#f59e0b', '#ec4899', '#22c55e', '#8b5cf6', '#06b6d4'] }];
  }

  loadProducts() { this.loadAll(); }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) this.dataSource.paginator.firstPage();
  }

  open() {
    this.dialog.open(ProductFormComponent, {
      width: '580px', maxHeight: '90vh', panelClass: 'custom-dialog', autoFocus: false
    }).afterClosed().subscribe(result => {
      if (result) this.PS.AddProduct(result).subscribe(() => this.loadAll());
    });
  }

  openEdit(id: number) {
    const param = new MatDialogConfig();
    param.data = id; param.width = '580px';
    param.maxHeight = '90vh'; param.panelClass = 'custom-dialog'; param.autoFocus = false;
    this.dialog.open(ProductFormComponent, param)
    .afterClosed().subscribe(result => {
      if (result) this.PS.UpdateProduct(id, result).subscribe(() => this.loadAll());
    });
  }

  deleteProduct(id: number) {
    if (confirm('Voulez-vous supprimer ce produit ?')) {
      this.PS.DeleteProduct(id).subscribe(() => this.loadAll());
    }
  }
}
