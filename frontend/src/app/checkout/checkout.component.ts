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
    if (!this.adresseLivraison || !this.ville || !this.codePostal) {
      alert('Veuillez remplir l’adresse complète');
      return;
    }

    const data = {
      adresseLivraison: this.adresseLivraison + ', ' + this.ville + ', ' + this.codePostal + ', ' + this.pays,
      modePaiement: this.modePaiement
    };

    this.OS.CreateOrder(data).subscribe({
      next: () => {
        alert('Commande créée avec succès');
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        console.log(err);
        alert('Erreur lors de la création de la commande');
      }
    });
  }
}