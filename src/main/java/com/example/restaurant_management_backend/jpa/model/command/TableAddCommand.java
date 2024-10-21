package com.example.restaurant_management_backend.jpa.model.command;

import com.example.restaurant_management_backend.common.SelfValidating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class TableAddCommand extends SelfValidating<TableAddCommand> {
    @NotNull(message = "Id stolika nie może być puste")
    private final String id;

    @Positive(message = "Pojemność stolika musi być większa od 0")
    private final int capacity;

    public TableAddCommand(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.validateSelf();
    }
}
