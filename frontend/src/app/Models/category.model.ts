export interface Category {
  id: number;
  nom: string;
  description: string;
  parentId?: number | null;
  parent?: Category;
}