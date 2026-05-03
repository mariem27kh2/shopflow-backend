import { Component, OnInit } from '@angular/core';
import { Product } from '../Models/product.model';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AuthPromptComponent } from '../auth-prompt/auth-prompt.component';

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {

  products: Product[] = [];
  addedProductId: number | null = null;

  constructor(
    private PS: ProductService,
    private CS: CartService,
    public AS: AuthService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.PS.GetAllProducts().subscribe((res: any) => {
      this.products = res.content ?? res;
    });
  }

  voirDetail(id: number) {
    this.router.navigate(['/product', id]);
  }

  peutAjouterPanier(): boolean {
    return !this.AS.isAdmin() && !this.AS.isSeller();
  }

  ajouterPanier(product: Product) {
    // Pas connecté → popup choix login / register
    if (!this.AS.isLoggedIn()) {
      this.dialog.open(AuthPromptComponent, {
        width: '400px',
        panelClass: 'custom-dialog',
        autoFocus: false
      });
      return;
    }

    this.CS.AddItem(product.id, 1).subscribe({
      next: () => {
        this.addedProductId = product.id;
        setTimeout(() => this.addedProductId = null, 2000);
      },
      error: () => {
        alert('Erreur lors de l\'ajout au panier');
      }
    });
  }
}
