package com.mani.loan.banking.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    @NotBlank(message = "Password is required")
    private String userPassword;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "name is required")
    private String fullName;

}
