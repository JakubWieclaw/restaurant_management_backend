package com.example.restaurant_management_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_management_backend.jpa.model.UserToken;
import com.example.restaurant_management_backend.services.UserTokenService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
@Validated
public class UserTokenController {

    private final UserTokenService userTokenService;

    private static final Logger logger = LoggerFactory.getLogger(UserTokenController.class);

    @Operation(summary = "Get all tokens")
    @GetMapping("/all")
    public ResponseEntity<List<UserToken>> getAllTokens() {
        logger.info("Getting all tokens");
        return ResponseEntity.ok(userTokenService.getAllTokens());
    }
}
