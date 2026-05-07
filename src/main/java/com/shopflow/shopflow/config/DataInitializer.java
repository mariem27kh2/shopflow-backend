package com.shopflow.shopflow.config;

import com.shopflow.shopflow.entity.*;
import com.shopflow.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ── Utilisateurs ──────────────────────────────────────────────
        if (userRepository.count() == 0) {

            User admin = User.builder()
                    .email("admin@shopflow.com")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .prenom("Admin").nom("ShopFlow")
                    .role(Role.ADMIN).actif(true).build();
            userRepository.save(admin);

            User seller1 = User.builder()
                    .email("techstore@shopflow.com")
                    .motDePasse(passwordEncoder.encode("seller123"))
                    .prenom("Mohamed").nom("Ben Ali")
                    .role(Role.SELLER).actif(true).build();
            userRepository.save(seller1);
            sellerProfileRepository.save(SellerProfile.builder()
                    .user(seller1).nomBoutique("TechStore Tunisie")
                    .description("Spécialiste en électronique et high-tech").note(4.8).build());

            User seller2 = User.builder()
                    .email("fashiontn@shopflow.com")
                    .motDePasse(passwordEncoder.encode("seller123"))
                    .prenom("Sarra").nom("Trabelsi")
                    .role(Role.SELLER).actif(true).build();
            userRepository.save(seller2);
            sellerProfileRepository.save(SellerProfile.builder()
                    .user(seller2).nomBoutique("Fashion TN")
                    .description("Mode et vêtements tendance").note(4.6).build());

            User customer1 = User.builder()
                    .email("client@shopflow.com")
                    .motDePasse(passwordEncoder.encode("client123"))
                    .prenom("Ahmed").nom("Khelifi")
                    .role(Role.CUSTOMER).actif(true).build();
            userRepository.save(customer1);

            User customer2 = User.builder()
                    .email("mariem@shopflow.com")
                    .motDePasse(passwordEncoder.encode("client123"))
                    .prenom("Mariem").nom("Bouaziz")
                    .role(Role.CUSTOMER).actif(true).build();
            userRepository.save(customer2);

            System.out.println("✅ Utilisateurs créés");
        }

        // ── Catégories ────────────────────────────────────────────────
        if (categoryRepository.count() == 0) {

            // Catégories principales
            Category elec = categoryRepository.save(Category.builder()
                    .nom("Électronique").description("Appareils électroniques et high-tech").build());
            Category mode = categoryRepository.save(Category.builder()
                    .nom("Mode").description("Vêtements, chaussures et accessoires").build());
            Category maison = categoryRepository.save(Category.builder()
                    .nom("Maison & Jardin").description("Décoration, mobilier et jardinage").build());
            Category sport = categoryRepository.save(Category.builder()
                    .nom("Sport & Loisirs").description("Équipements sportifs et loisirs").build());
            Category beaute = categoryRepository.save(Category.builder()
                    .nom("Beauté & Santé").description("Cosmétiques, soins et santé").build());

            // Sous-catégories Électronique
            Category smartphones = categoryRepository.save(Category.builder()
                    .nom("Smartphones").description("Téléphones mobiles").parent(elec).build());
            Category laptops = categoryRepository.save(Category.builder()
                    .nom("Laptops").description("Ordinateurs portables").parent(elec).build());
            Category audio = categoryRepository.save(Category.builder()
                    .nom("Audio & Son").description("Casques, enceintes et écouteurs").parent(elec).build());
            Category tv = categoryRepository.save(Category.builder()
                    .nom("TV & Écrans").description("Télévisions et moniteurs").parent(elec).build());

            // Sous-catégories Mode
            Category hommes = categoryRepository.save(Category.builder()
                    .nom("Homme").description("Mode masculine").parent(mode).build());
            Category femmes = categoryRepository.save(Category.builder()
                    .nom("Femme").description("Mode féminine").parent(mode).build());
            Category chaussures = categoryRepository.save(Category.builder()
                    .nom("Chaussures").description("Chaussures homme et femme").parent(mode).build());

            System.out.println("✅ Catégories créées");

            // ── Produits ──────────────────────────────────────────────
            if (productRepository.count() == 0) {

                User seller1 = userRepository.findByEmail("techstore@shopflow.com").orElse(null);
                User seller2 = userRepository.findByEmail("fashiontn@shopflow.com").orElse(null);

                if (seller1 != null) {

                    // ── SMARTPHONES ──
                    productRepository.save(Product.builder()
                            .nom("Samsung Galaxy S24 Ultra")
                            .description("Le flagship ultime de Samsung avec stylet S Pen intégré, écran Dynamic AMOLED 6.8\", processeur Snapdragon 8 Gen 3, 12 Go RAM, 256 Go stockage.")
                            .prix(4299.0).prixPromo(3899.0).stock(15).actif(true).seller(seller1)
                            .categories(List.of(smartphones, elec))
                            .images(List.of("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("iPhone 15 Pro Max")
                            .description("Apple iPhone 15 Pro Max avec puce A17 Pro, écran Super Retina XDR 6.7\", système de caméra pro 48MP, titane naturel.")
                            .prix(5499.0).prixPromo(4999.0).stock(10).actif(true).seller(seller1)
                            .categories(List.of(smartphones, elec))
                            .images(List.of("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Xiaomi Redmi Note 13 Pro")
                            .description("Smartphone milieu de gamme avec écran AMOLED 6.67\" 120Hz, appareil photo 200MP, batterie 5100mAh, charge rapide 67W.")
                            .prix(1299.0).prixPromo(1099.0).stock(30).actif(true).seller(seller1)
                            .categories(List.of(smartphones, elec))
                            .images(List.of("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&q=80"))
                            .build());

                    // ── LAPTOPS ──
                    productRepository.save(Product.builder()
                            .nom("MacBook Air M3 13\"")
                            .description("Ordinateur portable Apple avec puce M3, 8 Go RAM, 256 Go SSD, écran Liquid Retina 13.6\", autonomie 18h. Ultra-léger 1.24 kg.")
                            .prix(5999.0).prixPromo(5499.0).stock(8).actif(true).seller(seller1)
                            .categories(List.of(laptops, elec))
                            .images(List.of("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Dell XPS 15 OLED")
                            .description("Laptop premium avec écran OLED 15.6\" 3.5K, Intel Core i7-13700H, 16 Go DDR5, 512 Go NVMe SSD, NVIDIA RTX 4060.")
                            .prix(6499.0).stock(5).actif(true).seller(seller1)
                            .categories(List.of(laptops, elec))
                            .images(List.of("https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("HP Pavilion 15 Gaming")
                            .description("PC portable gaming avec AMD Ryzen 5 7535HS, 8 Go RAM, 512 Go SSD, NVIDIA GTX 1650, écran FHD 144Hz.")
                            .prix(2799.0).prixPromo(2499.0).stock(12).actif(true).seller(seller1)
                            .categories(List.of(laptops, elec))
                            .images(List.of("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&q=80"))
                            .build());

                    // ── AUDIO ──
                    productRepository.save(Product.builder()
                            .nom("Sony WH-1000XM5")
                            .description("Casque sans fil à réduction de bruit active leader du marché. Autonomie 30h, charge rapide, son Hi-Res Audio, confort premium.")
                            .prix(1299.0).prixPromo(999.0).stock(20).actif(true).seller(seller1)
                            .categories(List.of(audio, elec))
                            .images(List.of("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("AirPods Pro 2ème génération")
                            .description("Écouteurs Apple avec réduction de bruit active H2, audio spatial personnalisé, résistance à l'eau IPX4, autonomie 6h.")
                            .prix(1099.0).stock(25).actif(true).seller(seller1)
                            .categories(List.of(audio, elec))
                            .images(List.of("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("JBL Charge 5 Enceinte Bluetooth")
                            .description("Enceinte portable waterproof IP67, son puissant 40W, autonomie 20h, powerbank intégrée, connexion multi-appareils.")
                            .prix(699.0).prixPromo(599.0).stock(18).actif(true).seller(seller1)
                            .categories(List.of(audio, elec))
                            .images(List.of("https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&q=80"))
                            .build());

                    // ── TV ──
                    productRepository.save(Product.builder()
                            .nom("Samsung QLED 55\" 4K")
                            .description("Télévision QLED 55 pouces 4K UHD, processeur Neural Quantum, HDR10+, Smart TV Tizen, 4 ports HDMI 2.1, taux de rafraîchissement 120Hz.")
                            .prix(3499.0).prixPromo(2999.0).stock(6).actif(true).seller(seller1)
                            .categories(List.of(tv, elec))
                            .images(List.of("https://images.unsplash.com/photo-1593359677879-a4bb92f4834c?w=800&q=80"))
                            .build());
                }

                if (seller2 != null) {

                    // ── MODE HOMME ──
                    productRepository.save(Product.builder()
                            .nom("Veste en Cuir Homme Premium")
                            .description("Veste en cuir véritable coupe slim, doublure intérieure, fermeture éclair YKK, disponible en noir et marron. Tailles S à XXL.")
                            .prix(349.0).prixPromo(279.0).stock(20).actif(true).seller(seller2)
                            .categories(List.of(hommes, mode))
                            .images(List.of("https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Jean Slim Homme Stretch")
                            .description("Jean slim fit en denim stretch 98% coton 2% élasthanne, coupe moderne, délavage moyen, 5 poches. Tailles 28 à 38.")
                            .prix(129.0).prixPromo(89.0).stock(45).actif(true).seller(seller2)
                            .categories(List.of(hommes, mode))
                            .images(List.of("https://images.unsplash.com/photo-1542272604-787c3835535d?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Polo Ralph Lauren Classic")
                            .description("Polo classique en coton piqué 100%, broderie logo cheval, col côtelé, disponible en 8 coloris. Coupe regular fit.")
                            .prix(199.0).stock(35).actif(true).seller(seller2)
                            .categories(List.of(hommes, mode))
                            .images(List.of("https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=800&q=80"))
                            .build());

                    // ── MODE FEMME ──
                    productRepository.save(Product.builder()
                            .nom("Robe Midi Fleurie Femme")
                            .description("Robe midi à imprimé floral, tissu fluide viscose, col V, manches courtes, ceinture incluse. Parfaite pour l'été. Tailles XS à XL.")
                            .prix(159.0).prixPromo(119.0).stock(28).actif(true).seller(seller2)
                            .categories(List.of(femmes, mode))
                            .images(List.of("https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Sac à Main Cuir Femme")
                            .description("Sac à main en cuir synthétique premium, compartiment principal zippé, poche intérieure, bandoulière amovible. 30x20x12 cm.")
                            .prix(249.0).prixPromo(189.0).stock(15).actif(true).seller(seller2)
                            .categories(List.of(femmes, mode))
                            .images(List.of("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Abaya Moderne Brodée")
                            .description("Abaya élégante en crêpe de qualité supérieure, broderies dorées sur les manches, coupe droite moderne. Tailles 38 à 52.")
                            .prix(189.0).prixPromo(149.0).stock(22).actif(true).seller(seller2)
                            .categories(List.of(femmes, mode))
                            .images(List.of("https://images.unsplash.com/photo-1594938298603-c8148c4b4357?w=800&q=80"))
                            .build());

                    // ── CHAUSSURES ──
                    productRepository.save(Product.builder()
                            .nom("Nike Air Max 270")
                            .description("Chaussures de sport Nike Air Max 270, unité Air visible 270°, tige en mesh respirant, semelle intermédiaire en mousse. Pointures 38-46.")
                            .prix(599.0).prixPromo(499.0).stock(30).actif(true).seller(seller2)
                            .categories(List.of(chaussures, mode))
                            .images(List.of("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80"))
                            .build());

                    productRepository.save(Product.builder()
                            .nom("Adidas Stan Smith Blanc")
                            .description("Sneakers iconiques Adidas Stan Smith en cuir blanc, semelle caoutchouc, languette verte emblématique. Le classique indémodable.")
                            .prix(449.0).stock(25).actif(true).seller(seller2)
                            .categories(List.of(chaussures, mode))
                            .images(List.of("https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"))
                            .build());
                }

                System.out.println("✅ " + productRepository.count() + " produits créés");
            }
        }
    }
}
