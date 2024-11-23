package com.example.restaurant_management_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService customerUserDetailsService;
    private final JwtAuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults()) // by default use a bean by the name of corsConfigurationSource
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // endpoints available without authentication
                                "/swagger-ui/**", // swagger - to be removed in the end
                                "/v3/api-docs/**", // swagger
                                "/swagger-resources/**", // swagger
                                "/webjars/**", // swagger
                                "/auth/**",
                                "/api/categories/all",
                                "/api/categories/get/**",
                                "/admin/api/config/delivery-prices",
                                "/admin/api/config/opening-hours",
                                "/api/contact-form/send",
                                "api/categories/all",
                                "api/categories/get/**",
                                "api/customer/add",
                                "api/meals/all",
                                "api/meals/get**", // no slash in the end is intentional
                                "api/meals/search",
                                "api/opinions/average-rating/**",
                                "api/opinions/meal/**",
                                "api/orders/add",
                                "api/photos/download",

                                "/error")
                        .permitAll()

                        .requestMatchers( // endpoints available only for authenticated users
                                "api/customer/delete/**",
                                "api/customer/update/**",
                                "api/coupos/deactivate/**",
                                "api/coupons/validate",
                                "api/coupons/apply",
                                "api/coupons/customer/**",
                                "api/customer/get/**",
                                "api/opinions/add",
                                "api/opinions/customer/**",
                                "api/opinions/update",
                                "api/orders/customer/**",
                                "api/orders/delete/**",
                                "api/orders/update/**",
                                "api/orders/add-to-reservation",
                                "api/qr/table/**",
                                "api/tables/all",
                                "api/tables/**",
                                "api/reservations",
                                "api/reservations/day/**",
                                "api/reservations/customer/**",
                                "api/reservations/table/**",
                                "api/reservations/available-hours/**",
                                "api/reservations/**")
                        .hasAuthority("USER_PRIVILEGE")

                        .requestMatchers( // admin can access all endpoints
                                "**")
                        .hasAuthority("ADMIN_PRIVILEGE")

                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable) // enable this after testing in production
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customerUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider::authenticate;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // add frontend origins here
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
