import { Component, OnInit } from '@angular/core';
import { Product } from '../Models/product.model';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {

  product!: Product;
  added = false;

  constructor(
    private PS: ProductService,
    private CS: CartService,
    public AS: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.PS.GetProductById(id).subscribe((res: Product) => {
      this.product = res;
    });
  }

  retour() {
    this.router.navigate(['/shop']);
  }

  peutAjouterPanier(): boolean {
    return !this.AS.isAdmin() && !this.AS.isSeller();
  }

  ajouterPanier() {
    if (!this.AS.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.CS.AddItem(this.product.id, 1).subscribe({
      next: () => {
        this.added = true;
        setTimeout(() => this.added = false, 2000);
      },
      error: () => alert('Erreur ajout panier')
    });
  }
}