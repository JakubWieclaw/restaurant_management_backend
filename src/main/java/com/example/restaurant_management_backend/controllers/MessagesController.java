package com.example.restaurant_management_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {
    @GetMapping("/messages")
    public String getMessages() {
        return "[GET] Hello World!";
    }

    @PostMapping("/messages")
    public String postMessages() {
        return "[POST] Hello World!";
    }
}
