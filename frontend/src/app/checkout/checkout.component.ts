import { Component, OnInit } from '@angular/core';
import { Cart } from '../Models/cart.model';
import { CartService } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

  cart!: Cart;

  adresseLivraison: string = '';
  ville: string = '';
  codePostal: string = '';
  pays: string = 'Tunisie';
  modePaiement: string = 'CASH';
  errorMsg: string = '';

  constructor(
    private CS: CartService,
    private OS: OrderService,
    private router: Router
  ) {}

  ngOnInit() {
    this.CS.GetCart().subscribe(res => {
      this.cart = res;
    });
  }

  commander() {
    this.errorMsg = '';

    if (!this.adresseLivraison.trim()) {
      this.errorMsg = 'L\'adresse est obligatoire *';
      return;
    }
    if (!this.ville.trim()) {
      this.errorMsg = 'La ville est obligatoire *';
      return;
    }
    if (!this.codePostal.trim()) {
      this.errorMsg = 'Le code postal est obligatoire *';
      return;
    }

    const data = {
      adresseLivraison: this.adresseLivraison + ', ' + this.ville + ', ' + this.codePostal + ', ' + this.pays,
      modePaiement: this.modePaiement
    };

    this.OS.CreateOrder(data).subscribe({
      next: () => {
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        this.errorMsg = 'Erreur lors de la création de la commande';
        console.log(err);
      }
    });
  }
}
