import { Component, OnInit } from '@angular/core';
import { Product } from '../Models/product.model';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { ReviewService, Review } from '../services/review.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AuthPromptComponent } from '../auth-prompt/auth-prompt.component';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {

  product!: Product;
  added = false;

  // Reviews
  reviews: Review[] = [];
  showReviewForm = false;
  newNote: number = 5;
  newCommentaire: string = '';
  reviewSubmitted = false;
  reviewError = '';
  stars = [1, 2, 3, 4, 5];

  constructor(
    private PS: ProductService,
    private CS: CartService,
    public AS: AuthService,
    private RS: ReviewService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.PS.GetProductById(id).subscribe((res: Product) => {
      this.product = res;
      this.loadReviews();
    });
  }

  loadReviews() {
    this.RS.GetProductReviews(this.product.id).subscribe(res => {
      this.reviews = res.filter(r => r.approuve);
    });
  }

  retour() {
    this.router.navigate(['/shop']);
  }

  peutAjouterPanier(): boolean {
    return !this.AS.isAdmin() && !this.AS.isSeller();
  }

  peutLaisserAvis(): boolean {
    return this.AS.isCustomer();
  }

  ajouterPanier() {
    if (!this.AS.isLoggedIn()) {
      this.dialog.open(AuthPromptComponent, {
        width: '400px', panelClass: 'custom-dialog', autoFocus: false
      });
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

  setNote(n: number) {
    this.newNote = n;
  }

  submitReview() {
    this.reviewError = '';
    if (!this.newCommentaire.trim()) {
      this.reviewError = 'Veuillez écrire un commentaire';
      return;
    }

    this.RS.CreateReview({
      productId: this.product.id,
      note: this.newNote,
      commentaire: this.newCommentaire
    }).subscribe({
      next: () => {
        this.reviewSubmitted = true;
        this.showReviewForm = false;
        this.newCommentaire = '';
        this.newNote = 5;
      },
      error: (err) => {
        this.reviewError = err.error?.message || 'Erreur lors de l\'envoi';
      }
    });
  }

  getStars(note: number): string[] {
    return Array(5).fill('').map((_, i) => i < note ? 'star' : 'star_border');
  }

  getMoyenne(): number {
    if (this.reviews.length === 0) return 0;
    return Math.round(this.reviews.reduce((s, r) => s + r.note, 0) / this.reviews.length * 10) / 10;
  }
}
