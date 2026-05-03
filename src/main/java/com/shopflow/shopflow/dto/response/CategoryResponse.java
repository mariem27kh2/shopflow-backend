package com.shopflow.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String nom;
    private String description;
    private Long parentId;
}