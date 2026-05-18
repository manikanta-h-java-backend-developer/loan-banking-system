package com.mani.loan.banking.system.service;

import com.mani.loan.banking.system.dto.AuthResponseDTO;
import com.mani.loan.banking.system.dto.LoginRequestDTO;
import com.mani.loan.banking.system.dto.RegisterRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO registerRequestDTO);

    AuthResponseDTO userLogin(LoginRequestDTO loginRequestDTO);
}
