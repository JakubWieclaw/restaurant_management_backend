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

    public UserToken getUserTokenByTokenString(String tokenString) {
        List<UserToken> tokens = userTokenRepository.findAll();

        for (UserToken token : tokens) {
            if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                deleteToken(token);
                continue;
            }
            if (passwordEncoder.matches(tokenString, token.getTokenHash())) {
                return token;
            }
        }

        return null;
    }

    public Customer getUserByToken(String tokenString) {
        UserToken token = getUserTokenByTokenString(tokenString);

        if (token == null) {
            throw new NotFoundException("Podany token nie istnieje");
        }

        return token.getCustomer();
    }

    // public Customer getUserByToken(String tokenString) {
    // // Step 1: Extract salt from the token (assumes first 16 bytes are salt)
    // String salt = extractSaltFromToken(tokenString);

    // // Step 2: Query tokens by salt
    // List<UserToken> tokens = userTokenRepository.findTokensBySalt(salt);

    // // Step 3: Match token by hashing
    // for (UserToken token : tokens) {
    // String saltedToken = tokenString + token.getSalt();
    // if (passwordEncoder.matches(saltedToken, token.getTokenHash())) {
    // if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
    // throw new NotFoundException("Token wygasł");
    // }
    // return token.getCustomer();
    // }
    // }

    // throw new NotFoundException("Podany token nie istnieje");
    // }

    // private String extractSaltFromToken(String tokenString) {
    //     // Adjust this based on how the salt is embedded in your token string
    //     return tokenString.substring(0, 16); // Example: first 16 characters
    // }

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
        if (customer.getPrivilege().getPrivilegeName().equals("ADMIN_PRIVILEGE")) {
            return true;
        }
        if (customer.getPrivilege().getPrivilegeName().equals("USER_PRIVILEGE")
                && customer.getId().equals(requiredCustomerId)) {
            return true;
        }
        return false;
    }

    // public UserToken getUserTokenByTokenString(String tokenString) {
    //     String tokenHash = passwordEncoder.encode(tokenString);
    //     UserToken userToken = userTokenRepository.findByTokenHash(tokenHash);
    //     if (userToken == null) {
    //         throw new NotFoundException("Podany token nie istnieje");
    //     }
    //     return userToken;
    // }

    public List<UserToken> getAllTokens() {
        return userTokenRepository.findAll();
    }
}
