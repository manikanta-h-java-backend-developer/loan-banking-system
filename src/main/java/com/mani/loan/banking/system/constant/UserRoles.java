package com.mani.loan.banking.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoles {
    ADMIN("ADMIN"),
    LOAN_OFFICER("LOAN_OFFICER"),
    CUSTOMER("CUSTOMER");

    private final String userRole;
}
