package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.OpinionResponseDTO;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.ResourceConflictException;
import com.example.restaurant_management_backend.jpa.model.Opinion;
import com.example.restaurant_management_backend.jpa.model.command.OpinionAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.OpinionRepository;
import com.example.restaurant_management_backend.mappers.OpinionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpinionService {
    public static final String NOT_FOUND_OPINIONS = "Nie znaleziono opinii";
    public static final String DISH_NOT_FOUND = "Nie znaleziono dania";
    public static final String CLIENT_NOT_FOUND = "Nie znaleziono klienta";
    public static final String OPINION_ALREADY_EXISTS = "Opinia dla tego dania już istnieje";

    private final MealService mealService;
    private final OpinionRepository opinionRepository;
    private final CustomerUserDetailsService customerService;
    private final OpinionMapper opinionMapper;

    public OpinionResponseDTO addOpinion(OpinionAddCommand opinionAddCommand) {
        checkIfCustomerExists(opinionAddCommand.getCustomerId());

        if (opinionRepository.existsByMealIdAndCustomerId(opinionAddCommand.getMealId(), opinionAddCommand.getCustomerId())) {
            throw new ResourceConflictException(OPINION_ALREADY_EXISTS);
        }

        Opinion opinion = new Opinion();
        opinion.setMeal(mealService.getMealById(opinionAddCommand.getMealId()));
        opinion.setCustomer(customerService.getCustomerByIdOrThrowException(opinionAddCommand.getCustomerId()));
        opinion.setRating(opinionAddCommand.getRating());
        opinion.setComment(opinionAddCommand.getComment());

        opinionRepository.save(opinion);
        return opinionMapper.mapToDto(opinion);
    }

    public Optional<Double> getAverageRating(Long mealId) {
        List<Opinion> opinions = opinionRepository.findByMealId(mealId);
        return opinions.isEmpty()
                ? Optional.empty()
                : Optional.of(opinions.stream().mapToInt(Opinion::getRating).average().orElse(0.0));
    }

    public List<OpinionResponseDTO> getOpinionsForCustomer(Long customerId) {
        checkIfCustomerExists(customerId);
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
        checkIfCustomerExists(opinionAddCommand.getCustomerId());

        Opinion opinion = opinionRepository.findByMealIdAndCustomerId(opinionAddCommand.getMealId(), opinionAddCommand.getCustomerId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_OPINIONS));

        opinion.setRating(opinionAddCommand.getRating());
        opinion.setComment(opinionAddCommand.getComment());
        opinionRepository.save(opinion);

        return opinionMapper.mapToDto(opinion);
    }

    private void checkIfCustomerExists(Long customerId) {
        if (customerService.getCustomerById(customerId).isEmpty()) {
            throw new NotFoundException(CLIENT_NOT_FOUND);
        }
    }
}
