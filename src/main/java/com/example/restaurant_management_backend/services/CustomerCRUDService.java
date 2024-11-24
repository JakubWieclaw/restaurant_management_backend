package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.command.RegisterCustomerCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomerCRUDService {
    private final CustomerUserDetailsService customerService;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public void validateEmail(String email) {
        if (!emailService.validateEmailDomain(email)) {
            throw new IllegalArgumentException("Niepoprawna domena bądź nazwa użytkownika adresu email");
        }
    }

    public void deleteCustomerById(Long id) {
        customerService.checkIfCustomerIsNotTryingToAccessDifferentCustomer(id);
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Nie znaleziono klienta o id " + id);
        }
        customerRepository.deleteById(id);
    }

    public Customer updateCustomer(Long id, RegisterCustomerCommand registerCustomerCommand) {
        customerService.checkIfCustomerIsNotTryingToAccessDifferentCustomer(id);
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Nie znaleziono klienta o id " + id);
        }
        // validate email domain if it is changed
        Customer customer = customerService.getCustomerByIdOrThrowException(id);
        if (!customer.getEmail().equals(registerCustomerCommand.getEmail())) {
            validateEmail(registerCustomerCommand.getEmail());
        }
        customer.setName(registerCustomerCommand.getName());
        customer.setSurname(registerCustomerCommand.getSurname());
        customer.setEmail(registerCustomerCommand.getEmail());
        customer.setPhone(registerCustomerCommand.getPhone());
        // if new password is provided, encode it
        if (registerCustomerCommand.getPassword() != null) {
            customer.setPasswordHash(passwordEncoder.encode(registerCustomerCommand.getPassword()));
        }
        return customerRepository.save(customer);
    }

}
