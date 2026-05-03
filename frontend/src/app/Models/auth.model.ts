export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  email: string;
  prenom: string;
  nom: string;
  role: string;
}