package com.mani.loan.banking.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditLogActions {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    SUBMITTED("SUBMITTED"),
    DISBURSE("DISBURSE"),
    REPAYMENT_POSTED("PAYMENT_POSTED"),
    STATUS_CHANGE("STATUS_CHANGE"),
    ASSIGNED("ASSIGNED"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT");

    private final String action;
}
