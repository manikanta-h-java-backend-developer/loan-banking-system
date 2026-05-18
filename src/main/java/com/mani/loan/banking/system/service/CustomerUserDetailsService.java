package com.mani.loan.banking.system.service;

import com.mani.loan.banking.system.model.User;
import com.mani.loan.banking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load from YOUR database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getUserPassword())  // already BCrypt hashed
                .roles(String.valueOf(user.getUserRole())) // ADMIN, LOAN_OFFICER, CUSTOMER
                .accountExpired(false)
                .accountLocked(!user.getUserStatus()) // if userStatus is false, account is locked
                .credentialsExpired(false)
                .disabled(!user.getUserStatus()) // if userStatus is false, account is disabled
                .build();
    }
}
