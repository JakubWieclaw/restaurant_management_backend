package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.AverageRatingResponseDTO;
import com.example.restaurant_management_backend.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Opinion;
import com.example.restaurant_management_backend.jpa.model.command.OpinionAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.OpinionRepository;
import com.example.restaurant_management_backend.mappers.OpinionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpinionService {
    public static final String NOT_FOUND_OPINIONS = "Nie znaleziono opinii";
    public static final String OPINION_ALREADY_EXISTS = "Opinia dla tego dania już istnieje";

    private final MealService mealService;
    private final OpinionRepository opinionRepository;
    private final CustomerUserDetailsService customerService;
    private final OpinionMapper opinionMapper;

    public OpinionResponseDTO addOpinion(OpinionAddCommand opinionAddCommand) {
        customerService.checkIfCustomerIsNotTryingToAccessDifferentCustomer(opinionAddCommand.getCustomerId());
        Customer customer = customerService.getCustomerByIdOrThrowException(opinionAddCommand.getCustomerId());

        if (opinionRepository.existsByMealIdAndCustomerId(opinionAddCommand.getMealId(), customer.getId())) {
            throw new ResourceConflictException(OPINION_ALREADY_EXISTS);
        }

        Opinion opinion = new Opinion();
        opinion.setMeal(mealService.getMealById(opinionAddCommand.getMealId()));
        opinion.setCustomer(customer);
        opinion.setRating(opinionAddCommand.getRating());
        opinion.setComment(opinionAddCommand.getComment());

        opinionRepository.save(opinion);
        return opinionMapper.mapToDto(opinion);
    }

    public AverageRatingResponseDTO getAverageRating(Long mealId) {
        List<Opinion> opinions = opinionRepository.findByMealId(mealId);
        if (opinions.isEmpty()) {
            return new AverageRatingResponseDTO(null, 0);
        } else {
            double average = opinions.stream()
                    .mapToInt(Opinion::getRating)
                    .average().orElse(5.0); // 5.0 will never be displayed, it's just a placeholder
            return new AverageRatingResponseDTO(average, opinions.size());
        }
    }

    public List<OpinionResponseDTO> getOpinionsForCustomer(Long customerId) {
        customerService.checkIfCustomerIsNotTryingToAccessDifferentCustomer(customerId);
        return opinionRepository.findByCustomerId(customerId).stream()
                .map(opinionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OpinionResponseDTO> getOpinionsForMeal(Long mealId) {
        return opinionRepository.findByMealId(mealId).stream()
                .map(opinionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public OpinionResponseDTO updateOpinion(OpinionAddCommand opinionAddCommand) {
        Customer customer = customerService.getCurrentCustomer();

        Opinion opinion = opinionRepository.findByMealIdAndCustomerId(opinionAddCommand.getMealId(), customer.getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_OPINIONS));

        opinion.setRating(opinionAddCommand.getRating());
        opinion.setComment(opinionAddCommand.getComment());
        opinionRepository.save(opinion);

        return opinionMapper.mapToDto(opinion);
    }
}
