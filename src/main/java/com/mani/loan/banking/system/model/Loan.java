package com.mani.loan.banking.system.model;

import com.mani.loan.banking.system.constant.LoanStatus;
import com.mani.loan.banking.system.constant.LoanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loans", schema = "dbo")
public class Loan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include // Ensures `loanId` is included in Lombok-generated `equals()` and `hashCode()` so entity identity is based on the primary key.
    private Long loanId;

    /**
     * The customer who applied for this loan.
     * FK → [dbo].[customers].[id]
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_loans_customer_id"),
            nullable = false
    )
    @NotNull(message = "Customer must be associated with a loan")
    private Customer customer;

    /**
     * Type of loan applied for.
     * Stored as string in DB; validated via enum on application layer.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @Column(name = "principal_amount", nullable = false)
    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "1000.00", message = "Principal amount must be at least 1000")
    @DecimalMax(value = "99999999.99", message = "Principal amount exceeds maximum limit")
    @Digits(integer = 16, fraction = 2, message = "Invalid principal amount format")
    private BigDecimal principalAmount;

    /**
     * Annual interest rate applied to the loan.
     * Example: 8.50 means 8.50% per annum.
     */
    @Column(name = "interest_rate", nullable = false)
    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    @DecimalMax(value = "100.00", message = "Interest rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Invalid interest rate format")
    private BigDecimal interestRate;

    /**
     * Loan repayment tenure in months.
     * Example: 60 = 5 years.
     */
    @Column(name = "tenure_months", nullable = false)
    @NotNull(message = "Tenure is required")
    @Min(value = 1,   message = "Tenure must be at least 1 month")
    @Max(value = 360, message = "Tenure cannot exceed 360 months (30 years)")
    private Integer tenureMonths;

    /**
     * Current lifecycle status of the loan.
     * Default is PENDING on first application.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Loan status is required")
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    /**
     * Timestamp when the loan application was submitted.
     * Auto-populated by DB default (getdate()) and Hibernate on insert.
     * Never updated after creation.
     */
    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false,
            columnDefinition = "datetime default getdate()")
    private LocalDateTime appliedAt;

    /**
     * The user (Loan Officer / Admin) who approved or rejected the loan.
     * FK → [dbo].[users].[id]
     * Nullable — not yet actioned when loan is first applied.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "approved_by",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_loans_approved_by"),
            nullable = true
    )
    private User approvedByUser;

    /**
     * Timestamp when the loan was approved or rejected by a Loan Officer / Admin.
     * Null until actioned.
     */
    @Column(name = "approved_at", nullable = true)
    private LocalDateTime approvedAt;

    /**
     * Optional notes added by Loan Officer during approval or rejection.
     * Example: "Rejected due to low credit score."
     */
    @Column(name = "remarks", nullable = true)
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

}
