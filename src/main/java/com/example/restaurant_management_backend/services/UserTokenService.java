package com.example.restaurant_management_backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.exceptions.UnauthorizedException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.UserToken;
import com.example.restaurant_management_backend.jpa.repositories.UserTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    private final PasswordEncoder passwordEncoder;

    public UserToken addToken(UserToken userToken) {
        return userTokenRepository.save(userToken);
    }

    public void deleteToken(UserToken userToken) {
        userTokenRepository.delete(userToken);
    }

    // public Customer getCustomerByToken(String tokenString) {
    // String tokenHash = passwordEncoder.encode(tokenString);

    // final var token = userTokenRepository.findByTokenHash(tokenHash);
    // if (token == null) {
    // throw new NotFoundException("Podany token nie istnieje");
    // }
    // if (token.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
    // throw new NotFoundException("Token wygasł");
    // }

    // final var customer = userTokenRepository.findCustomerByTokenHash(tokenHash);
    // if (customer == null) {
    // throw new NotFoundException("Klient z podanym tokenem nie istnieje");
    // }
    // return customer;
    // }

    public Customer getUserByToken(String tokenString) {
        // Step 1: Extract salt from the token (assumes first 16 bytes are salt)
        String salt = extractSaltFromToken(tokenString);

        // Step 2: Query tokens by salt
        List<UserToken> tokens = userTokenRepository.findTokensBySalt(salt);

        // Step 3: Match token by hashing
        for (UserToken token : tokens) {
            String saltedToken = tokenString + token.getSalt();
            if (passwordEncoder.matches(saltedToken, token.getTokenHash())) {
                if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                    throw new NotFoundException("Token wygasł");
                }
                return token.getCustomer();
            }
        }

        throw new NotFoundException("Podany token nie istnieje");
    }

    private String extractSaltFromToken(String tokenString) {
        // Adjust this based on how the salt is embedded in your token string
        return tokenString.substring(0, 16); // Example: first 16 characters
    }

    public void checkAccess(UserToken token, Customer customer, String requiredPrivilegeName, Long requiredCustomerId) {
        if (!canCustomerAccessResource(token, customer, requiredPrivilegeName, requiredCustomerId)) {
            throw new UnauthorizedException("Brak dostępu do zasobu");
        }
    }

    private boolean canCustomerAccessResource(UserToken token, Customer customer, String requiredPrivilegeName,
            Long requiredCustomerId) {
        if (token.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }
        if (customer.getPrivilege().getPrivilegeName().equals(requiredPrivilegeName)) {
            return true;
        }
        if (customer.getPrivilege().getPrivilegeName().equals(requiredPrivilegeName)
                && customer.getId().equals(requiredCustomerId)) {
            return true;
        }
        return false;
    }

    public UserToken getUserTokenByTokenString(String tokenString) {
        String tokenHash = passwordEncoder.encode(tokenString);
        UserToken userToken = userTokenRepository.findByTokenHash(tokenHash);
        if (userToken == null) {
            throw new NotFoundException("Podany token nie istnieje");
        }
        return userToken;
    }

    public List<UserToken> getAllTokens() {
        return userTokenRepository.findAll();
    }
}
