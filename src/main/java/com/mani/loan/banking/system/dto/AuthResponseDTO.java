package com.mani.loan.banking.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String role;
}
