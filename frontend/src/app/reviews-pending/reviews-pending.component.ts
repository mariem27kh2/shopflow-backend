import { Component, OnInit } from '@angular/core';
import { ReviewService, Review } from '../services/review.service';

@Component({
  selector: 'app-reviews-pending',
  templateUrl: './reviews-pending.component.html',
  styleUrls: ['./reviews-pending.component.css']
})
export class ReviewsPendingComponent implements OnInit {

  reviews: Review[] = [];
  loading = true;

  constructor(private RS: ReviewService) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.loading = true;
    this.RS.GetPendingReviews().subscribe(res => {
      this.reviews = res;
      this.loading = false;
    });
  }

  approve(id: number) {
    this.RS.ApproveReview(id).subscribe(() => {
      this.reviews = this.reviews.filter(r => r.id !== id);
    });
  }

  getStars(note: number): string[] {
    return Array(5).fill('').map((_, i) => i < note ? 'star' : 'star_border');
  }
}
