package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.dto.RegisterResponseDTO;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testCreateAuthSuccess() throws Exception {

        when(authService.registerUser(any(RegisterUserCommand.class)))
                .thenReturn(new RegisterResponseDTO(1L, "Anna", "Annowa", "abc@wp.pl", "123123123"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Anna\", " +
                                "\"surname\": \"Annowa\", " +
                                "\"email\": \"abc@wp.pl\", " +
                                "\"phone\": \"123123123\", " +
                                "\"password\": \"123123123\"}"))
                .andExpect(status().isCreated());
    }
}
