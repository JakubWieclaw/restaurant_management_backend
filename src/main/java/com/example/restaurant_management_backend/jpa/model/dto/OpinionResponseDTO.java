package com.example.restaurant_management_backend.jpa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OpinionResponseDTO {
    private Long customerId;
    private Integer rating;
    private String comment;
}
