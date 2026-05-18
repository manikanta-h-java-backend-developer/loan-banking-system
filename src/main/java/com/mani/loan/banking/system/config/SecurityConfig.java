package com.mani.loan.banking.system.config;

import com.mani.loan.banking.system.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomerUserDetailsService customerUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                // Disable CSRF for REST APIs (stateless — no sessions)
                csrf(csrf -> csrf.disable())
                // Define which endpoints are public vs protected
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() //public
//                        .requestMatchers("api/admin/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "api/loans/apply").hasRole("CUSTOMER")
//                        .requestMatchers("/api/loans/*/approve").hasAnyRole("ADMIN", "LOAN_OFFICER")
//                        .requestMatchers("/api/loans/**").hasAnyRole("ADMIN","LOAN_OFFICER", "CUSTOMER")
                        .anyRequest().authenticated()
                )
                // Tell Spring this is stateless (no sessions — JWT handles state)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Register your JWT filter BEFORE Spring's default auth filter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customerUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Password encoder — always BCrypt, never plain text
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager — needed to trigger authentication manually
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
