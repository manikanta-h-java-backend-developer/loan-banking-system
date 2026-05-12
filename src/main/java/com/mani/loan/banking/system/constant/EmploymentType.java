package com.mani.loan.banking.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmploymentType {
    SALARIED("Salaried"),
    SELF_EMPLOYED("Self-Employed"),
    UNEMPLOYED("Unemployed"),
    STUDENT("Student"),
    RETIRED("Retired");

    private final String employmentType;
}
