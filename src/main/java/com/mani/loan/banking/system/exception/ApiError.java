package com.mani.loan.banking.system.exception;

import java.time.Instant;

public record ApiError(
        String code,
        String message,
        int status,
        String path,
        Instant timestamp,
        String traceId
) {}