package com.mani.loan.banking.system.model;

import com.mani.loan.banking.system.constant.PaymentMethods;
import com.mani.loan.banking.system.constant.RepaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_repayments", schema = "dbo")
public class LoanRepayment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long repaymentId;

    /**
     * The loan associated with this repayment.
     * FK → [dbo].[loans].[id]
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "loan_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_loan_repayments_loan_id"),
            nullable = false
    )
    private Loan loan;

    @Column(name = "emi_amount", nullable = false)
    private BigDecimal emiAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date", nullable = true)
    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RepaymentStatus paymentStatus; // e.g., "PAID", "PENDING", "OVERDUE"

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = true)
    private PaymentMethods paymentMethod; // e.g., "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", etc.
}