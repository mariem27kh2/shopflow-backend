export interface Product {
  id: number;
  nom: string;
  description: string;
  prix: number;
  prixPromo?: number;
  stock: number;
  actif: boolean;
  dateCreation: string;
  sellerNom: string;
  sellerId: number;
  categories: string[];
  images: string[];
  noteMoyenne: number;
  nombreAvis: number;
  pourcentageRemise?: number;
}

export interface ProductRequest {
  nom: string;
  description: string;
  prix: number;
  prixPromo?: number;
  stock: number;
  categorieIds: number[];
  images: string[];
}