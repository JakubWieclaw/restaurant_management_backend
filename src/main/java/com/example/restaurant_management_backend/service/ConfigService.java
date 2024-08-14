package com.example.restaurant_management_backend.service;

import com.example.restaurant_management_backend.jpa.model.Config;
import com.example.restaurant_management_backend.jpa.repositories.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;

    public void initialize(Config config) {
        configRepository.save(config);
    }
}
