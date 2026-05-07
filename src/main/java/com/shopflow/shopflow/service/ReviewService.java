package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.request.ReviewRequest;
import com.shopflow.shopflow.dto.response.ReviewResponse;
import com.shopflow.shopflow.entity.*;
import com.shopflow.shopflow.exception.BusinessException;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
    }
// Méthode utilitaire pour convertir une entité Review en DTO ReviewResponse, en incluant le nom complet du client, la note, le commentaire, la date de création, et le statut d'approbation de l'avis
    private ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setCustomerNom(review.getCustomer().getPrenom()
                + " " + review.getCustomer().getNom());
        response.setNote(review.getNote());
        response.setCommentaire(review.getCommentaire());
        response.setDateCreation(review.getDateCreation());
        response.setApprouve(review.isApprouve());
        return response;
    }
// Méthode pour créer un avis sur un produit, en vérifiant que le client a acheté le produit avant de permettre la création de l'avis, en s'assurant qu'un client ne peut laisser qu'un seul avis par produit, et en enregistrant l'avis avec un statut d'approbation initial à false pour une modération ultérieure
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User customer = getCurrentUser();

        // Vérifier que le client a acheté ce produit
        boolean aAchete = orderRepository
                .findByCustomerId(customer.getId(),
                        org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .anyMatch(order -> order.getLignes().stream()
                        .anyMatch(item -> item.getProduct().getId()
                                .equals(request.getProductId())));

        if (!aAchete) {
            throw new BusinessException(
                    "Vous devez acheter ce produit avant de laisser un avis !");
        }

        if (reviewRepository.existsByCustomerIdAndProductId(
                customer.getId(), request.getProductId())) {
            throw new BusinessException("Vous avez déjà laissé un avis !");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé"));

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .approuve(false)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprouveTrue(productId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository.findByApprouveFalse()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }
// Méthode pour approuver un avis, en mettant à jour le statut d'approbation de l'avis à true pour le rendre visible sur la plateforme, et en retournant la réponse avec les détails de l'avis approuvé
    @Transactional
    public ReviewResponse approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avis non trouvé"));
        review.setApprouve(true);
        return toResponse(reviewRepository.save(review));
    }
}