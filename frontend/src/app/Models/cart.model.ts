export interface CartItem {
  id: number;
  productId: number;
  productNom: string;
  productPrix: number;
  productImage: string;
  quantite: number;
  sousTotal: number;
}

export interface Cart {
  id: number;
  lignes: CartItem[];
  sousTotal: number;
  fraisLivraison: number;
  remise: number;
  totalTTC: number;
  codeCoupon?: string;
}
