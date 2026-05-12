package com.mani.loan.banking.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepaymentStatus {
    PAID("Paid"),
    PENDING("Pending"),
    OVERDUE("Overdue");
    private final String repaymentStatus;

}
