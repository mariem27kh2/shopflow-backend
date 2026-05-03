import { Component, OnInit } from '@angular/core';
import { Category } from '../Models/category.model';
import { CategoryService } from '../services/category.service';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {

  categories: Category[] = [];

  nom: string = '';
  description: string = '';
  parentId: number | null = null;

  editMode: boolean = false;
  editId!: number;

  showForm: boolean = false;

  constructor(private CS: CategoryService) {}

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.CS.GetAllCategories().subscribe(res => {
      console.log('CATEGORIES:', res);
      this.categories = res;
    });
  }

  openDialog(category?: Category) {
    if (category) {
      this.editMode = true;
      this.editId = category.id;
      this.nom = category.nom;
      this.description = category.description;
      this.parentId = category.parentId ?? null;
    } else {
      this.resetForm();
      this.editMode = false;
    }

    this.showForm = true;
  }

  closeForm() {
    this.resetForm();
    this.showForm = false;
  }

  saveCategory() {
    if (!this.nom.trim()) {
      alert('Nom obligatoire');
      return;
    }

    const data: any = {
      nom: this.nom,
      description: this.description
    };

    if (this.parentId !== null) {
      data.parentId = this.parentId;
    }

    console.log('DATA SENT:', data);

    if (this.editMode) {
      this.CS.UpdateCategory(this.editId, data).subscribe(() => {
        this.closeForm();
        this.loadCategories();
      });
    } else {
      this.CS.AddCategory(data).subscribe(() => {
        this.closeForm();
        this.loadCategories();
      });
    }
  }

  deleteCategory(id: number) {
    if (confirm('Voulez-vous supprimer cette catégorie ?')) {
      this.CS.DeleteCategory(id).subscribe(() => {
        this.loadCategories();
      });
    }
  }

  getChildren(parentId: number): Category[] {
    return this.categories.filter(c => c.parentId === parentId);
  }

  resetForm() {
    this.nom = '';
    this.description = '';
    this.parentId = null;
    this.editMode = false;
  }
}