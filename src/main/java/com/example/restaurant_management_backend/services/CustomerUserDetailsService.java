package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.command.RegisterUserCommand;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        Set<GrantedAuthority> authorities = convertPrivilegesToAuthorities(customer.getPrivilege());

        return new org.springframework.security.core.userdetails.User(customer.getEmail(), customer.getPasswordHash(), authorities);
    }

    private Set<GrantedAuthority> convertPrivilegesToAuthorities(Privilege privilege) {
        return Set.of(new SimpleGrantedAuthority(privilege.getPrivilegeName()));
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer getCustomerByIdOrThrowException(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono klienta o id " + id));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByEmailOrThrowException(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono klienta o adresie e-mail " + email));
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> getCustomerByResetToken(String resetToken) {
        return customerRepository.findByResetToken(resetToken);
    }

    public Customer getCustomerByResetTokenOrThrowException(String resetToken) {
        return customerRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono klienta z tokenem resetującym " + resetToken));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomerById(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Nie znaleziono klienta o id " + id);
        }
        customerRepository.deleteById(id);
    }

    public Customer updateCustomer(Long id, RegisterUserCommand registerUserCommand) {
        Customer customer = getCustomerByIdOrThrowException(id);
        customer.setName(registerUserCommand.getName());
        customer.setSurname(registerUserCommand.getSurname());
        customer.setEmail(registerUserCommand.getEmail());
        customer.setPhone(registerUserCommand.getPhone());
        customer.setPasswordHash(passwordEncoder.encode(registerUserCommand.getPassword()));
        return customerRepository.save(customer);
    }
}
