package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Opinion;
import com.example.restaurant_management_backend.jpa.model.command.OpinionAddCommand;
import com.example.restaurant_management_backend.jpa.model.dto.OpinionResponseDTO;
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
    public static final String DISH_NOT_FOUND = "Nie znaleziono dania";
    public static final String CLIENT_NOT_FOUND = "Nie znaleziono klienta";
    public static final String OPINION_ALREADY_EXISTS = "Opinia dla tego dania już istnieje";
    private final MealService mealService;
    private final OpinionRepository opinionRepository;
    private final CustomerUserDetailsService customerService;
    private final OpinionMapper opinionMapper;

    public OpinionResponseDTO addOpinion(OpinionAddCommand opinionAddCommand) {
        checkIfClientAndMealExists(opinionAddCommand);
        if (opinionRepository.existsByMealIdAndCustomerId(opinionAddCommand.getMealId(), opinionAddCommand.getCustomerId())) {
            throw new IllegalArgumentException(OPINION_ALREADY_EXISTS);
        }

        Opinion opinion = new Opinion();
        opinion.setMeal(mealService.getMealById(opinionAddCommand.getMealId()).get());
        opinion.setCustomer(customerService.getCustomerById(opinionAddCommand.getCustomerId()).get());
        opinion.setRating(opinionAddCommand.getRating());
        opinion.setComment(opinionAddCommand.getComment());
        opinionRepository.save(opinion);

        return opinionMapper.mapToDto(opinion);
    }

    public double getAverageRating(Long mealId) {
        checkIfMealExists(mealId);
        final List<Opinion> opinions = opinionRepository.findAll();
        return opinions.stream()
                .filter(opinion -> opinion.getMeal().getId().equals(mealId))
                .mapToInt(Opinion::getRating)
                .average()
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_OPINIONS));
    }

    public List<OpinionResponseDTO> getOpinionsForCustomer(Long customerId) {
        checkIfCustomerExists(customerId);
        final List<Opinion> opinions = opinionRepository.findAll();

        if (opinions.isEmpty()) {
            throw new IllegalArgumentException(NOT_FOUND_OPINIONS);
        }

        return opinions.stream()
                .filter(opinion -> opinion.getCustomer().getId().equals(customerId))
                .map(opinionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OpinionResponseDTO> getOpinionsForMeal(Long mealId) {
        checkIfMealExists(mealId);
        final List<Opinion> opinions = opinionRepository.findAll();

        if (opinions.isEmpty()) {
            throw new IllegalArgumentException(NOT_FOUND_OPINIONS);
        }

        return opinions.stream()
                .filter(opinion -> opinion.getMeal().getId().equals(mealId))
                .map(opinionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public OpinionResponseDTO updateOpinion(OpinionAddCommand opinionAddCommand) {
        checkIfClientAndMealExists(opinionAddCommand);

        if (!opinionRepository.existsByMealIdAndCustomerId(opinionAddCommand.getMealId(), opinionAddCommand.getCustomerId())) {
            throw new IllegalArgumentException(NOT_FOUND_OPINIONS);
        }

        Opinion opinion = opinionRepository.findAll().stream()
                .filter(op -> op.getMeal().getId().equals(opinionAddCommand.getMealId()))
                .filter(op -> op.getCustomer().getId().equals(opinionAddCommand.getCustomerId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_OPINIONS));

        opinion.setRating(opinionAddCommand.getRating());
        opinion.setComment(opinionAddCommand.getComment());
        opinionRepository.save(opinion);

        return opinionMapper.mapToDto(opinion);
    }

    private void checkIfClientAndMealExists(OpinionAddCommand opinionAddCommand) {
        checkIfMealExists(opinionAddCommand.getMealId());
        checkIfCustomerExists(opinionAddCommand.getCustomerId());
    }

    private void checkIfMealExists(Long mealId) {
        if (mealService.getMealById(mealId).isEmpty()) {
            throw new NotFoundException(DISH_NOT_FOUND);
        }
    }

    private void checkIfCustomerExists(Long customerId) {
        if (customerService.getCustomerById(customerId).isEmpty()) {
            throw new NotFoundException(CLIENT_NOT_FOUND);
        }
    }
}
