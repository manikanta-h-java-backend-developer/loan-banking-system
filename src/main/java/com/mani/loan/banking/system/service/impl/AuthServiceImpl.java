package com.mani.loan.banking.system.service.impl;

import com.mani.loan.banking.system.constant.UserRoles;
import com.mani.loan.banking.system.dto.AuthResponseDTO;
import com.mani.loan.banking.system.dto.LoginRequestDTO;
import com.mani.loan.banking.system.dto.RegisterRequestDTO;
import com.mani.loan.banking.system.exception.DuplicateResourceException;
import com.mani.loan.banking.system.model.User;
import com.mani.loan.banking.system.repository.UserRepository;
import com.mani.loan.banking.system.service.AuthService;
import com.mani.loan.banking.system.service.CustomerUserDetailsService;
import com.mani.loan.banking.system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerUserDetailsService customerUserDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        Optional<User> existingUser = userRepository.findByUsername(registerRequestDTO.getUsername());
        // Check duplicate username
        if (existingUser.isPresent()) {
            throw new DuplicateResourceException(
                    "Username already exists");
        }

        // Build and save user
        User user = User.builder()
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .fullName(registerRequestDTO.getFullName())
                .userPassword(passwordEncoder.encode(registerRequestDTO.getUserPassword()))
                .userRole(UserRoles.CUSTOMER) // default role
                .userStatus(true)
                .build();
        userRepository.save(user);

        // Generate token
        UserDetails userDetails =
                customerUserDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .accessToken(token)
                .username(user.getUsername())
                .role(user.getUserRole().name())
                .build();
    }

    @Override
    public AuthResponseDTO userLogin(LoginRequestDTO loginRequestDTO) {
        // This triggers the full Spring Security auth flow
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getUserPassword()
                )
        );

        // If we reach here — credentials are valid
        UserDetails userDetails =
                customerUserDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository
                .findByUsername(loginRequestDTO.getUsername()).orElseThrow();

        return AuthResponseDTO.builder()
                .accessToken(token)
                .username(user.getUsername())
                .role(user.getUserRole().name())
                .build();
    }
}
