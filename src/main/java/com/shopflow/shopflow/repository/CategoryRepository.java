package com.shopflow.shopflow.repository;

import com.shopflow.shopflow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Catégories racines (sans parent)
    List<Category> findByParentIsNull();
}