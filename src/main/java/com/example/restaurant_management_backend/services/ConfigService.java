package com.example.restaurant_management_backend.services;

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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;
    private final DeliveryPriceRepository deliveryPriceRepository;
    private final OpeningHourRepository openingHourRepository;
    private final ConfigMapper configMapper;

    public boolean isSystemInitialized() {
        return !configRepository.findAll().isEmpty();
    }

    @Transactional
    public void initialize(ConfigAddCommand configAddCommand) {
        if (isSystemInitialized()) {
            throw new IllegalArgumentException("System został już zainicjalizowany");
        }

        deliveryPriceRepository.saveAll(configAddCommand.getDeliveryPricings());
        openingHourRepository.saveAll(configAddCommand.getOpeningHours());
        configRepository.save(configMapper.toConfig(configAddCommand));
    }

    public Config getConfig() {
        return configRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("System nie został zainicjalizowany"));
    }

    public List<DeliveryPricing> getDeliveryPrices() {
        if (!isSystemInitialized()) {
            throw new IllegalArgumentException("System nie został zainicjalizowany");
        }
        return deliveryPriceRepository.findAll();
    }

    public List<OpeningHour> openingHours() {
        if (!isSystemInitialized()) {
            throw new IllegalArgumentException("System nie został zainicjalizowany");
        }
        return openingHourRepository.findAll();
    }

    @Transactional
    public void removeAll() {
        if (!isSystemInitialized()) {
            throw new IllegalArgumentException("Nie ma wgranej żadnej konfiguracji");
        }

        deliveryPriceRepository.deleteAll();
        openingHourRepository.deleteAll();
        configRepository.deleteAll();
    }
}
