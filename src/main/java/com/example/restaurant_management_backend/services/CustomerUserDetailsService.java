package com.example.restaurant_management_backend.services;

// import com.example.restaurant_management_backend.exceptions.AccessDeniedException;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.Customer;
import com.example.restaurant_management_backend.jpa.model.Privilege;
import com.example.restaurant_management_backend.jpa.model.PrivilegeName;
import com.example.restaurant_management_backend.jpa.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    public static final String NOT_FOUND_CUSTOMER_WITH_EMAIL = "Nie znaleziono klienta z email: ";
    public static final String NOT_FOUND_CLIENT_ID = "Nie znaleziono klienta o id ";
    public static final String NOT_FOUND_CLIENT_WITH_RESET_TOKEN = "Nie znaleziono klienta z tokenem resetującym ";
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_CUSTOMER_WITH_EMAIL + email));
        Set<GrantedAuthority> authorities = convertPrivilegesToAuthorities(customer.getPrivilege());

        return new org.springframework.security.core.userdetails.User(customer.getEmail(), customer.getPasswordHash(),
                authorities);
    }

    private Set<GrantedAuthority> convertPrivilegesToAuthorities(Privilege privilege) {
        return Set.of(new SimpleGrantedAuthority(privilege.getPrivilegeName().name()));
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer getCustomerByIdOrThrowException(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CLIENT_ID + id));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByEmailOrThrowException(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER_WITH_EMAIL + email));
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // public Customer getCustomerByIdForAuthenticatedUser(Long id) { // EXPERIMENT, MAY BE USEFUL LATER OR NOT
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUserEmail = authentication.getName(); // Extract user info from token

    //     final var currentCustomerPrivileges = customerRepository.findByEmail(currentUserEmail).get().getPrivilege();

    //     Customer customer = customerRepository.findById(id)
    //             .orElseThrow(() -> new NotFoundException(NOT_FOUND_CLIENT_ID + id));

    //     // if current user is not an admin and tries to access other user's data
    //     if (!currentCustomerPrivileges.getPrivilegeName().equals(PrivilegeName.ADMIN_PRIVILEGE)
    //             && !customer.getEmail().equals(currentUserEmail)) {
    //         throw new AccessDeniedException("{}");
    //     }

    //     return customer;
    // }

    public Optional<Customer> getCustomerByResetToken(String resetToken) {
        return customerRepository.findByResetToken(resetToken);
    }

    public Customer getCustomerByResetTokenOrThrowException(String resetToken) {
        return customerRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CLIENT_WITH_RESET_TOKEN + resetToken));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public long countAll() {
        return customerRepository.count();
    }
}
