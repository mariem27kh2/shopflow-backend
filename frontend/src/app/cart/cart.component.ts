import { Component, OnInit } from '@angular/core';
import { Cart } from '../Models/cart.model';
import { CartService } from '../services/cart.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  cart!: Cart;

  constructor(private CS: CartService) {}

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.CS.GetCart().subscribe(res => {
      this.cart = res;
    });
  }

  plus(itemId: number, quantite: number) {
    this.CS.UpdateItem(itemId, quantite + 1).subscribe(res => {
      this.cart = res;
    });
  }

  moins(itemId: number, quantite: number) {
    if (quantite > 1) {
      this.CS.UpdateItem(itemId, quantite - 1).subscribe(res => {
        this.cart = res;
      });
    }
  }

  supprimer(itemId: number) {
    this.CS.RemoveItem(itemId).subscribe(res => {
      this.cart = res;
    });
  }
}
