import { Component, Inject, NgZone, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ProductService } from '../services/product.service';
import { CategoryService } from '../services/category.service';
import { Category } from '../Models/category.model';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {

  form!: FormGroup;
  categories: Category[] = [];
  isEdit = false;

  imagePreview: string = '';
  imageBase64: string = '';

  constructor(
    private fb: FormBuilder,
    private PS: ProductService,
    private CS: CategoryService,
    private zone: NgZone,
    private dialogRef: MatDialogRef<ProductFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: number
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      nom: ['', Validators.required],
      description: [''],
      prix: [0, Validators.required],
      prixPromo: [null],
      stock: [0, Validators.required],
      categorieIds: [[]]
    });

    this.CS.GetAllCategories().subscribe(res => {
      this.categories = res;
    });

    if (this.data) {
      this.isEdit = true;
      this.PS.GetProductById(this.data).subscribe(product => {
        const firstImage = product.images && product.images.length > 0 ? product.images[0] : '';
        this.imagePreview = firstImage;
        this.imageBase64 = firstImage;
        this.form.patchValue({
          nom: product.nom,
          description: product.description,
          prix: product.prix,
          prixPromo: product.prixPromo,
          stock: product.stock,
          categorieIds: []
        });
      });
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      alert('Veuillez sélectionner une image (jpg, png, webp...)');
      return;
    }

    // Réduire la taille de l'image avant envoi
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const img = new Image();
      img.onload = () => {
        // Redimensionner à max 800px
        const canvas = document.createElement('canvas');
        const MAX = 800;
        let w = img.width;
        let h = img.height;

        if (w > MAX || h > MAX) {
          if (w > h) { h = Math.round(h * MAX / w); w = MAX; }
          else { w = Math.round(w * MAX / h); h = MAX; }
        }

        canvas.width = w;
        canvas.height = h;
        const ctx = canvas.getContext('2d')!;
        ctx.drawImage(img, 0, 0, w, h);

        const compressed = canvas.toDataURL('image/jpeg', 0.7);

        // NgZone pour déclencher la détection de changements Angular
        this.zone.run(() => {
          this.imagePreview = compressed;
          this.imageBase64 = compressed;
        });
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  removeImage() {
    this.imagePreview = '';
    this.imageBase64 = '';
  }

  save() {
    if (this.form.invalid) return;

    const result = {
      nom: this.form.get('nom')?.value,
      description: this.form.get('description')?.value,
      prix: this.form.get('prix')?.value,
      prixPromo: this.form.get('prixPromo')?.value || null,
      stock: this.form.get('stock')?.value,
      categorieIds: this.form.get('categorieIds')?.value || [],
      images: this.imageBase64 ? [this.imageBase64] : []
    };

    this.dialogRef.close(result);
  }

  close() {
    this.dialogRef.close();
  }
}
