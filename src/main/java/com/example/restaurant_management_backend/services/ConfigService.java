package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.SystemAlreadyInitializedException;
import com.example.restaurant_management_backend.exceptions.SystemNotInitializedException;
import com.example.restaurant_management_backend.jpa.model.Config;
import com.example.restaurant_management_backend.jpa.model.DeliveryPricing;
import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import com.example.restaurant_management_backend.jpa.model.command.ConfigAddCommand;
import com.example.restaurant_management_backend.jpa.repositories.ConfigRepository;
import com.example.restaurant_management_backend.jpa.repositories.DeliveryPriceRepository;
import com.example.restaurant_management_backend.jpa.repositories.OpeningHourRepository;
import com.example.restaurant_management_backend.mappers.ConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {
    public static final String SYSTEM_WAS_INITIALIZED = "System został już zainicjalizowany.";
    public static final String SYSTEM_NOT_INITIALIZED = "Proszę zainicjalizować system.";
    private final ConfigRepository configRepository;
    private final DeliveryPriceRepository deliveryPriceRepository;
    private final OpeningHourRepository openingHourRepository;
    private final ConfigMapper configMapper;

    private boolean isSystemInitialized() {
        return configRepository.count() > 0;
    }

    private void ensureSystemNotInitialized() {
        if (isSystemInitialized()) {
            throw new SystemAlreadyInitializedException(SYSTEM_WAS_INITIALIZED);
        }
    }

    private void ensureSystemInitialized() {
        if (!isSystemInitialized()) {
            throw new SystemNotInitializedException(SYSTEM_NOT_INITIALIZED);

        }
    }

    @Transactional
    public void initialize(ConfigAddCommand configAddCommand) {
        ensureSystemNotInitialized();

        deliveryPriceRepository.saveAll(configAddCommand.getDeliveryPricings());
        openingHourRepository.saveAll(configAddCommand.getOpeningHours());
        configRepository.save(configMapper.toConfig(configAddCommand));
    }

    public Config getConfig() {
        return configRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new SystemNotInitializedException(SYSTEM_NOT_INITIALIZED));
    }

    public List<DeliveryPricing> getDeliveryPrices() {
        ensureSystemInitialized();
        return deliveryPriceRepository.findAll();
    }

    public List<OpeningHour> getOpeningHours() {
        ensureSystemInitialized();
        return openingHourRepository.findAll();
    }

    @Transactional
    public void removeAll() {
        ensureSystemInitialized();

        deliveryPriceRepository.deleteAll();
        openingHourRepository.deleteAll();
        configRepository.deleteAll();
    }
}
