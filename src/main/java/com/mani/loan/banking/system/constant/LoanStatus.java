package com.mani.loan.banking.system.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    DISBURSED("Disbursed"),
    CLOSED("Closed");

    private final String loanStatus;
}
