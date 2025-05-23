package com.example.restaurant_management_backend.mappers;

import com.example.restaurant_management_backend.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.jpa.model.Opinion;
import org.springframework.stereotype.Component;

@Component
public class OpinionMapper {
    public OpinionResponseDTO mapToDto(Opinion opinion) {
        return new OpinionResponseDTO(
                opinion.getCustomer().getId(),
                opinion.getRating(),
                opinion.getComment()
        );
    }
}
