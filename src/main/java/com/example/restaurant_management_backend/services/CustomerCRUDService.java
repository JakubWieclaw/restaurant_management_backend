package com.example.restaurant_management_backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.command.RegisterCustomerCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import com.example.restaurant_management_backend.security.EmailValidator;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class CustomerCRUDService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator = new EmailValidator();


    public void validateEmail(String email) {
        if (!emailValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Niepoprawna domena bądź nazwa użytkownika adresu email");
        }
    }

    public void deleteCustomerById(Long id) {
    if (!customerRepository.existsById(id)) {
        throw new NotFoundException("Nie znaleziono klienta o id " + id);
    }
    customerRepository.deleteById(id);
    }

    public Customer updateCustomer(Long id, RegisterCustomerCommand registerCustomerCommand) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Nie znaleziono klienta o id " + id);
        }
        // validate email domain if it is changed
        if (!customerRepository.findById(id).get().getEmail().equals(registerCustomerCommand.getEmail())) {
            validateEmail(registerCustomerCommand.getEmail());
        }
        Customer customer = customerRepository.findById(id).get();
        customer.setName(registerCustomerCommand.getName());
        customer.setSurname(registerCustomerCommand.getSurname());
        customer.setEmail(registerCustomerCommand.getEmail());
        customer.setPhone(registerCustomerCommand.getPhone());
        customer.setPasswordHash(passwordEncoder.encode(registerCustomerCommand.getPassword()));
        return customerRepository.save(customer);
    }

    // private boolean validateEmailBody(String email) {
    //     String domain = email.substring(email.indexOf('@') + 1);
    //     try {
    //         Lookup lookup = new Lookup(domain, Type.MX);
    //         Record[] records = lookup.run();
    //         if (records!= null && records.length > 0) {
    //             MXRecord mxRecord = (MXRecord) records[0];
    //             return true; // Domain has a valid MX record
    //         }
    //     } catch (TextParseException e) {
    //         throw new NotFoundException("Nie znaleziono domeny email");
    //     }
    //     return false; // Domain does not have a valid MX record
    // }

}
