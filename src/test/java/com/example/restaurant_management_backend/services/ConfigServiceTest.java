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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {

    @InjectMocks
    private ConfigService configService;

    @Mock
    private ConfigRepository configRepository;

    @Mock
    private DeliveryPriceRepository deliveryPriceRepository;

    @Mock
    private OpeningHourRepository openingHourRepository;

    @Mock
    private ConfigMapper configMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void initialize_whenSystemNotInitialized_shouldSaveConfigAndDependencies() {
        // Given
        ConfigAddCommand configAddCommand = mock(ConfigAddCommand.class);
        List<DeliveryPricing> deliveryPricings = List.of(mock(DeliveryPricing.class));
        List<OpeningHour> openingHours = List.of(mock(OpeningHour.class));
        Config config = mock(Config.class);

        when(configRepository.count()).thenReturn(0L);
        when(configAddCommand.getDeliveryPricings()).thenReturn(deliveryPricings);
        when(configAddCommand.getOpeningHours()).thenReturn(openingHours);
        when(configMapper.toConfig(configAddCommand)).thenReturn(config);

        // When
        configService.initialize(configAddCommand);

        // Then
        verify(deliveryPriceRepository, times(1)).saveAll(deliveryPricings);
        verify(openingHourRepository, times(1)).saveAll(openingHours);
        verify(configRepository, times(1)).save(config);
    }

    @Test
    public void initialize_whenSystemInitialized_shouldThrowException() {
        // Given
        ConfigAddCommand configAddCommand = mock(ConfigAddCommand.class);
        when(configRepository.count()).thenReturn(1L);

        // When & Then
        assertThrows(SystemAlreadyInitializedException.class, () -> configService.initialize(configAddCommand));
        verifyNoInteractions(deliveryPriceRepository, openingHourRepository, configMapper);
    }

    @Test
    public void getConfig_whenSystemInitialized_shouldReturnConfig() {
        // Given
        Config config = mock(Config.class);
        when(configRepository.findAll()).thenReturn(List.of(config));

        // When
        Config result = configService.getConfig();

        // Then
        assertNotNull(result);
        assertEquals(config, result);
    }

    @Test
    public void getConfig_whenSystemNotInitialized_shouldThrowException() {
        // Given
        when(configRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(SystemNotInitializedException.class, () -> configService.getConfig());
    }

    @Test
    public void getDeliveryPrices_whenSystemInitialized_shouldReturnDeliveryPrices() {
        // Given
        List<DeliveryPricing> deliveryPricings = List.of(mock(DeliveryPricing.class));
        when(configRepository.count()).thenReturn(1L);
        when(deliveryPriceRepository.findAll()).thenReturn(deliveryPricings);

        // When
        List<DeliveryPricing> result = configService.getDeliveryPrices();

        // Then
        assertNotNull(result);
        assertEquals(deliveryPricings, result);
    }

    @Test
    public void getDeliveryPrices_whenSystemNotInitialized_shouldThrowException() {
        // Given
        when(configRepository.count()).thenReturn(0L);

        // When & Then
        assertThrows(SystemNotInitializedException.class, () -> configService.getDeliveryPrices());
        verifyNoInteractions(deliveryPriceRepository);
    }

    @Test
    public void getOpeningHours_whenSystemInitialized_shouldReturnOpeningHours() {
        // Given
        List<OpeningHour> openingHours = List.of(mock(OpeningHour.class));
        when(configRepository.count()).thenReturn(1L);
        when(openingHourRepository.findAll()).thenReturn(openingHours);

        // When
        List<OpeningHour> result = configService.getOpeningHours();

        // Then
        assertNotNull(result);
        assertEquals(openingHours, result);
    }

    @Test
    public void getOpeningHours_whenSystemNotInitialized_shouldThrowException() {
        // Given
        when(configRepository.count()).thenReturn(0L);

        // When & Then
        assertThrows(SystemNotInitializedException.class, () -> configService.getOpeningHours());
        verifyNoInteractions(openingHourRepository);
    }

    @Test
    public void removeAll_whenSystemInitialized_shouldDeleteAll() {
        // Given
        when(configRepository.count()).thenReturn(1L);

        // When
        configService.removeAll();

        // Then
        verify(deliveryPriceRepository, times(1)).deleteAll();
        verify(openingHourRepository, times(1)).deleteAll();
        verify(configRepository, times(1)).deleteAll();
    }

    @Test
    public void removeAll_whenSystemNotInitialized_shouldThrowException() {
        // Given
        when(configRepository.count()).thenReturn(0L);

        // When & Then
        assertThrows(SystemNotInitializedException.class, () -> configService.removeAll());
        verifyNoInteractions(deliveryPriceRepository, openingHourRepository);
    }
}
