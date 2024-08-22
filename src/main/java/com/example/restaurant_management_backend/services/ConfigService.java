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

import java.util.List;

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

    public void initialize(ConfigAddCommand configAddCommand) {
        if (isSystemInitialized()) {
            return;
        }
        deliveryPriceRepository.saveAll(configAddCommand.getDeliveryPricings());
        openingHourRepository.saveAll(configAddCommand.getOpeningHours());
        configRepository.save(configMapper.toConfig(configAddCommand));
    }

    public Config getConfig() {
        List<Config> configs = configRepository.findAll();
        if (configs.isEmpty()) {
            return null;
        }
        return configs.getFirst();
    }

    public List<DeliveryPricing> getDeliveryPrices() {
        return deliveryPriceRepository.findAll();
    }

    public List<OpeningHour> openingHours() {
        return openingHourRepository.findAll();
    }

    public void removeAll() {
        deliveryPriceRepository.deleteAll();
        openingHourRepository.deleteAll();

        configRepository.deleteAll();
    }
}
