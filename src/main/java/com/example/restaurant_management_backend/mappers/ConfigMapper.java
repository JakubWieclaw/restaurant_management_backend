package com.example.restaurant_management_backend.mappers;

import com.example.restaurant_management_backend.jpa.model.Config;
import com.example.restaurant_management_backend.jpa.model.command.ConfigAddCommand;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

    public Config toConfig(ConfigAddCommand configAddCommand) {
        Config config = new Config();
        config.setCity(configAddCommand.getCity());
        config.setEmail(configAddCommand.getEmail());
        config.setRestaurantName(configAddCommand.getRestaurantName());
        config.setPostalCode(configAddCommand.getPostalCode());
        config.setStreet(configAddCommand.getStreet());
        config.setPhoneNumber(configAddCommand.getPhoneNumber());
        config.setLogoUrl(configAddCommand.getLogoUrl());
        return config;
    }
}
