package com.mani.loan.banking.system.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanType {

    PERSONAL("Personal Loan"),
    HOME("Home Loan"),
    AUTO("Auto Loan"),
    EDUCATION("Education Loan"),
    BUSINESS("Business Loan");

    private final String loanTypeName;
}
