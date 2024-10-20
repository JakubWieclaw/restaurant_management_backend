package com.example.restaurant_management_backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableServiceTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exampleTest() {
        // adding tests just in case, but for now there is no need
        assertEquals(11, "Hello world".length());
    }

}