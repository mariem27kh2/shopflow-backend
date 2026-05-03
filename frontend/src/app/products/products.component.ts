import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { Product } from '../Models/product.model';
import { ProductService } from '../services/product.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ProductFormComponent } from '../product-form/product-form.component';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  dataSource = new MatTableDataSource<Product>();

  displayedColumns: string[] = [
    'image',
    'id',
    'nom',
    'prix',
    'prixPromo',
    'stock',
    'actif',
    'categories',
    'actions'
  ];

  constructor(private PS: ProductService, private dialog: MatDialog) {}

  ngOnInit() {
    this.loadProducts();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadProducts() {
  this.PS.GetAllProducts().subscribe((res: any) => {
    console.log("PRODUCTS:", res);

    this.dataSource.data = res.content ?? res;
  });
}

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  open() {
    this.dialog.open(ProductFormComponent, {
      width: '580px',
      maxHeight: '90vh',
      panelClass: 'custom-dialog',
      autoFocus: false
    })
    .afterClosed()
    .subscribe(result => {
      if (result) {
        console.log('Données envoyées:', result);
        this.PS.AddProduct(result).subscribe({
          next: (res) => {
            console.log('Produit créé:', res);
            this.loadProducts();
          },
          error: (err) => {
            console.error('Erreur création:', err);
            alert('Erreur: ' + (err.error?.message || err.status));
          }
        });
      }
    });
  }

  openEdit(id: number) {
    const param = new MatDialogConfig();
    param.data = id;
    param.width = '580px';
    param.maxHeight = '90vh';
    param.panelClass = 'custom-dialog';
    param.autoFocus = false;

    this.dialog.open(ProductFormComponent, param)
    .afterClosed()
    .subscribe(result => {
      if (result) {
        this.PS.UpdateProduct(id, result).subscribe(() => {
          this.loadProducts();
        });
      }
    });
  }

  deleteProduct(id: number) {
    if (confirm('Voulez-vous supprimer ce produit ?')) {
      this.PS.DeleteProduct(id).subscribe(() => {
        this.loadProducts();
      });
    }
  }
}