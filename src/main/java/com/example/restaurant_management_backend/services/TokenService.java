package com.example.restaurant_management_backend.services;

import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.UserToken;
import com.example.restaurant_management_backend.jpa.repositories.UserTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenService {

    private final UserTokenRepository userTokenRepository;

    public UserToken addToken(UserToken userToken) {
        return userTokenRepository.save(userToken);
    }

    public Customer getCustomerByToken(String token) {
        final var customer = userTokenRepository.findByTokenHash(token);
        if (customer == null) {
            throw new NotFoundException("Nie znaleziono klienta z podanym tokenem");
        }
        return customer;
    }

    public boolean canCustomerAccessResource(Customer customer, Privilege requiredPrivilege, Long customerId) {
        if (customer.getPrivilege().getPrivilegeName().equals("ADMIN_PRIVILEGE")) {
            return true;
        }
        if (customer.getPrivilege().getPrivilegeName().equals("USER_PRIVILEGE") && customer.getId().equals(customerId)) {
            return true;
        }
        return false;
    }
}
