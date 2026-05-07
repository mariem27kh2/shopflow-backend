# Rapport Technique — Projet ShopFlow
## Application Web E-Commerce Full-Stack

---

**Étudiant :** ___________________________  
**Filière :** Développement Web Full-Stack  
**Année universitaire :** 2025-2026  
**Date :** Mai 2026

---

## Table des matières

1. Introduction et présentation du projet
2. Schéma d'architecture technique
3. Description des choix d'implémentation
4. Difficultés rencontrées et solutions apportées
5. Répartition des tâches
6. Conclusion

---

## 1. Introduction et présentation du projet

### 1.1 Contexte

ShopFlow est une application web e-commerce complète développée dans le cadre du projet d'examen. Elle permet la gestion d'une boutique en ligne avec trois types d'utilisateurs : administrateur, vendeur et client.

### 1.2 Fonctionnalités principales

| Fonctionnalité | Description |
|---|---|
| Authentification JWT | Connexion, inscription, gestion des rôles |
| Boutique publique | Affichage des produits, recherche, filtres |
| Panier d'achat | Ajout, modification, suppression d'articles |
| Commandes | Passage de commande, suivi, annulation |
| Dashboard Admin | Statistiques, gestion produits, catégories, commandes |
| Dashboard Seller | Mes produits, mes commandes clients, revenus |
| Avis clients | Soumission, modération par l'admin |
| Pagination | Toutes les listes sont paginées côté backend |

### 1.3 Technologies utilisées

**Backend :**
- Java 21 + Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- Base de données H2 (fichier persistant)
- Maven

**Frontend :**
- Angular 16
- Angular Material (UI Components)
- Chart.js / ng2-charts (visualisation)
- TypeScript

---

## 2. Schéma d'architecture technique

### 2.1 Architecture générale (3 couches)

```
┌─────────────────────────────────────────────────────────┐
│                    FRONTEND Angular 16                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐  │
│  │Components│  │ Services │  │  Guards  │  │Models  │  │
│  │(HTML/CSS)│  │(HTTP)    │  │(Auth)    │  │(TS)    │  │
│  └────┬─────┘  └────┬─────┘  └──────────┘  └────────┘  │
│       │              │ HTTP + JWT Token                  │
└───────┼──────────────┼──────────────────────────────────┘
        │              │
        ▼              ▼
┌─────────────────────────────────────────────────────────┐
│              BACKEND Spring Boot 3.5                     │
│  ┌──────────────────────────────────────────────────┐   │
│  │              SecurityConfig (CORS + JWT)          │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │Controller│→ │ Service  │→ │Repository│              │
│  │(REST API)│  │(Logique) │  │(JPA)     │              │
│  └──────────┘  └──────────┘  └────┬─────┘              │
└──────────────────────────────────┼──────────────────────┘
                                   │ SQL
                                   ▼
                    ┌──────────────────────────┐
                    │   Base de données H2      │
                    │   (fichier persistant)    │
                    │   data/shopflowdb.mv.db   │
                    └──────────────────────────┘
```

### 2.2 Flux de données — Exemple : Passer une commande

```
Client Angular
    │
    ├─ 1. POST /api/orders (avec token JWT dans le header)
    │
    ▼
JwtAuthFilter (intercepte la requête)
    │
    ├─ 2. Vérifie le token → extrait l'email
    │
    ▼
OrderController
    │
    ├─ 3. Reçoit OrderRequest (adresse, mode paiement)
    │
    ▼
OrderService
    │
    ├─ 4. Récupère le panier du client
    ├─ 5. Vérifie le stock de chaque produit
    ├─ 6. Calcule le total + frais de livraison
    ├─ 7. Crée l'entité Order + OrderItems
    ├─ 8. Décrémente le stock des produits
    ├─ 9. Vide le panier
    │
    ▼
OrderRepository (JPA)
    │
    ├─ 10. INSERT INTO orders ...
    ├─ 11. INSERT INTO order_items ...
    │
    ▼
OrderResponse (DTO)
    │
    └─ 12. HTTP 200 + JSON → Angular affiche confirmation
```

### 2.3 Modèle de données — Relations JPA

```
User ──────────── OneToOne ──────────── SellerProfile
User ──────────── OneToMany ─────────── Order (customer)
User ──────────── OneToOne ──────────── Cart

Product ────────── ManyToOne ────────── User (seller)
Product ────────── ManyToMany ──────── Category
Product ────────── OneToMany ─────────── Review

Order ──────────── ManyToOne ────────── User (customer)
Order ──────────── OneToMany ─────────── OrderItem

OrderItem ──────── ManyToOne ────────── Product

Cart ───────────── OneToOne ──────────── User
Cart ───────────── OneToMany ─────────── CartItem

CartItem ────────── ManyToOne ────────── Product

Category ────────── ManyToOne ────────── Category (parent)
Category ────────── OneToMany ─────────── Category (sousCategories)
```

---

## 3. Description des choix d'implémentation les plus complexes

### 3.1 Authentification JWT (JSON Web Token)

**Choix :** Utilisation de JWT stateless au lieu des sessions HTTP.

**Implémentation :**

```java
// JwtAuthFilter.java — s'exécute à chaque requête
@Override
protected void doFilterInternal(HttpServletRequest request, ...) {
    String token = request.getHeader("Authorization").substring(7);
    String email = jwtService.extractEmail(token);
    // Charge l'utilisateur et l'authentifie dans Spring Security
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

**Côté Angular — Intercepteur HTTP :**

```typescript
// auth.interceptor.ts
intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = this.AS.getToken();
    if (token) {
        const cloned = req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
        });
        return next.handle(cloned);
    }
    return next.handle(req);
}
```

**Justification :** JWT permet une architecture stateless — le serveur ne stocke pas les sessions, ce qui facilite la scalabilité.

---

### 3.2 Gestion des rôles et contrôle d'accès

**Trois rôles distincts :** ADMIN, SELLER, CUSTOMER

**Backend — `@PreAuthorize` :**
```java
@GetMapping
@PreAuthorize("hasRole('ADMIN')")  // Admin uniquement
public ResponseEntity<Page<OrderResponse>> getAllOrders(...) { }

@PostMapping
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")  // Seller et Admin
public ResponseEntity<ProductResponse> createProduct(...) { }
```

**Frontend — Guards Angular :**
```typescript
// adminSellerGuard.ts
export const adminSellerGuard: CanActivateFn = () => {
    if (AS.isAdmin() || AS.isSeller()) return true;
    if (AS.isLoggedIn()) router.navigate(['/shop']);
    else router.navigate(['/login']);
    return false;
};
```

**Frontend — Layout conditionnel :**
- Admin/Seller → sidebar avec dashboard
- Client → navbar simple avec boutique
- Visiteur → boutique publique sans authentification

---

### 3.3 Pagination JPA

**Choix :** Pagination côté serveur pour éviter de charger toutes les données.

```java
// ProductService.java
public Page<ProductResponse> getAllProducts(int page, int size) {
    Pageable pageable = PageRequest.of(
        page, size,
        Sort.by("dateCreation").descending()
    );
    // SQL généré : SELECT * FROM products LIMIT 10 OFFSET 0
    return productRepository.findByActifTrue(pageable)
            .map(this::toResponse);
}
```

**Réponse paginée :**
```json
{
  "content": [...],
  "totalElements": 18,
  "totalPages": 2,
  "size": 10,
  "number": 0
}
```

**Angular extrait le contenu :**
```typescript
GetAllProducts(): Observable<Product[]> {
    return this.http.get<any>(`${this.apiUrl}?page=0&size=100`).pipe(
        map(res => res.content ? res.content : res)
    );
}
```

---

### 3.4 Upload d'images en Base64

**Problème :** Stocker des images produits sans serveur de fichiers.

**Solution :** Conversion en Base64 côté Angular avec compression via Canvas.

```typescript
// product-form.component.ts
onFileSelected(event: Event) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
        const img = new Image();
        img.onload = () => {
            // Redimensionner à max 800px
            const canvas = document.createElement('canvas');
            canvas.width = 800; canvas.height = 600;
            const ctx = canvas.getContext('2d')!;
            ctx.drawImage(img, 0, 0, 800, 600);
            // Compression JPEG 70%
            const compressed = canvas.toDataURL('image/jpeg', 0.7);
            this.zone.run(() => {
                this.imagePreview = compressed;
                this.imageBase64 = compressed;
            });
        };
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);
}
```

**Stockage :** La chaîne Base64 est stockée dans la colonne `IMAGE_URL TEXT` de H2.

**Problème rencontré :** La colonne était `VARCHAR(255)` — trop courte pour le Base64. Solution : `@Column(columnDefinition = "TEXT")`.

---

### 3.5 Dashboard dynamique selon le rôle

Le même composant `DashboardComponent` affiche des données différentes selon le rôle connecté :

```typescript
ngOnInit() {
    this.isAdmin = this.AS.isAdmin();
    this.isSeller = this.AS.isSeller();
    this.loadAll();
}

loadAll() {
    if (this.isAdmin) {
        this.loadAdminDashboard(); // produits + catégories + commandes + vendeurs
    } else {
        this.loadSellerDashboard(); // mes produits + mes commandes + revenus
    }
}
```

**Visualisation :** 4 graphiques Chart.js (bar, pie, doughnut, pie) mis à jour dynamiquement avec auto-refresh toutes les 30 secondes via `interval(30000)`.

---

## 4. Difficultés rencontrées et solutions apportées

### 4.1 LazyInitializationException

**Problème :** Hibernate chargeait les entités en mode LAZY (par défaut). Quand Jackson essayait de sérialiser les données en JSON, la session Hibernate était déjà fermée.

```
LazyInitializationException: could not initialize proxy
[com.shopflow.entity.User#1] - no session
```

**Solution :** Passage en `FetchType.EAGER` sur les relations nécessaires.

```java
// Avant (problème)
@ManyToOne(fetch = FetchType.LAZY)
private User seller;

// Après (solution)
@ManyToOne(fetch = FetchType.EAGER)
private User seller;
```

**Entités corrigées :** Product, Order, Cart, CartItem, OrderItem, Review.

**Note :** La bonne pratique serait d'utiliser `@Transactional` + `JOIN FETCH`, mais EAGER était la solution la plus adaptée pour ce projet.

---

### 4.2 CORS (Cross-Origin Resource Sharing)

**Problème :** Le navigateur bloquait les requêtes Angular (port 4200) vers Spring Boot (port 8081).

**Solution :** Configuration CORS dans `SecurityConfig.java` :

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

---

### 4.3 Détection de changements Angular (NgZone)

**Problème :** `FileReader.onload` s'exécute en dehors de la zone Angular. L'aperçu de l'image ne s'affichait pas après sélection.

**Solution :** Utilisation de `NgZone.run()` pour forcer la détection de changements.

```typescript
reader.onload = (e: any) => {
    img.onload = () => {
        const compressed = canvas.toDataURL('image/jpeg', 0.7);
        this.zone.run(() => {  // ← Force Angular à détecter le changement
            this.imagePreview = compressed;
        });
    };
};
```

---

### 4.4 Persistance des données H2

**Problème :** La base de données H2 en mode mémoire (`mem:`) perdait toutes les données à chaque redémarrage.

**Solution :** Passage en mode fichier persistant.

```properties
# Avant (données perdues au redémarrage)
spring.datasource.url=jdbc:h2:mem:shopflowdb

# Après (données persistantes)
spring.datasource.url=jdbc:h2:file:./data/shopflowdb;AUTO_SERVER=TRUE
spring.jpa.hibernate.ddl-auto=update
```

---

### 4.5 Modèle Cart — Incompatibilité Frontend/Backend

**Problème :** Le backend retournait `productNom`, `productPrix` directement dans `CartItem`, mais le frontend attendait un objet `product` imbriqué.

**Solution :** Mise à jour du modèle Angular pour correspondre à la réponse réelle du backend.

```typescript
// Avant (incorrect)
export interface CartItem {
    product: { nom: string; prix: number; };
}

// Après (correct — correspond au DTO backend)
export interface CartItem {
    productId: number;
    productNom: string;
    productPrix: number;
    productImage: string;
    quantite: number;
    sousTotal: number;
}
```

---

## 5. Répartition des tâches

> *(À compléter si projet en binôme)*

| Tâche | Responsable | Statut |
|---|---|---|
| Configuration Spring Boot + Sécurité JWT | | ✅ |
| Entités JPA et relations | | ✅ |
| Controllers REST (Auth, Product, Order) | | ✅ |
| Controllers REST (Cart, Category, Review) | | ✅ |
| Configuration Angular + Routing | | ✅ |
| Composants Login / Register | | ✅ |
| Boutique publique + Panier | | ✅ |
| Dashboard Admin + Charts | | ✅ |
| Dashboard Seller | | ✅ |
| Gestion Commandes Admin/Seller | | ✅ |
| Système d'avis clients | | ✅ |
| Design UI (Angular Material) | | ✅ |
| Tests et débogage | | ✅ |
| Déploiement Firebase | | ✅ |

---

## 6. Conclusion

Le projet ShopFlow a permis de mettre en pratique l'ensemble des concepts du développement web full-stack :

**Côté Backend :**
- Architecture en couches (Controller → Service → Repository)
- Sécurité avec Spring Security et JWT
- Gestion des relations JPA (OneToMany, ManyToMany, OneToOne)
- Pagination et tri avec Spring Data

**Côté Frontend :**
- Architecture modulaire Angular (composants, services, guards)
- Communication REST avec HttpClient et intercepteurs
- Visualisation de données avec Chart.js
- Interface responsive avec Angular Material

**Points d'amélioration possibles :**
- Remplacer H2 par PostgreSQL ou MySQL en production
- Utiliser LAZY loading avec `@Transactional` + `JOIN FETCH`
- Ajouter des tests unitaires (JUnit, Jasmine)
- Déployer le backend sur Railway ou Render

---

*Rapport généré pour le projet d'examen — ShopFlow E-Commerce Application*
