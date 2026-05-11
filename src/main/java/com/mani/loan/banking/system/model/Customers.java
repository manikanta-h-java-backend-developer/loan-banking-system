package com.mani.loan.banking.system.model;

import com.mani.loan.banking.system.constant.EmploymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "loans"})
@Table(name = "customers", schema = "dbo")
public class Customers implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    // -------------------------------------------------------------------------
    // Relationship — User Account
    // -------------------------------------------------------------------------

    /**
     * The system user account linked to this customer.
     * FK → [dbo].[users].[id]
     * Nullable — a customer profile can exist before a user account is created.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_customers_user_id"),
            nullable = true
    )
    private User user;

    // -------------------------------------------------------------------------
    // Relationship — Loans (Bidirectional)
    // -------------------------------------------------------------------------

    /**
     * All loan applications submitted by this customer.
     * Mapped by the 'customer' field in the Loan entity.
     * Use with caution — always fetch lazily; never iterate in a loop without pagination.
     */
    @OneToMany(
            mappedBy = "customer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    @Builder.Default
    private List<Loans> loans = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Personal Details
    // -------------------------------------------------------------------------

    /**
     * Full legal name of the customer.
     */
    @Column(name = "full_name", nullable = false, length = 200)
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 200, message = "Full name must be between 2 and 200 characters")
    @Pattern(
            regexp = "^[a-zA-Z\\s.'-]+$",
            message = "Full name can only contain letters, spaces, dots, apostrophes, and hyphens"
    )
    private String fullName;

    /**
     * Unique email address of the customer.
     * Has a UNIQUE NONCLUSTERED index in DB — enforced at both DB and application level.
     */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    /**
     * Customer's contact phone number.
     * Nullable — optional at registration.
     */
    @Column(name = "phone", nullable = true, length = 20)
    @Pattern(
            regexp = "^[+]?[0-9\\-\\s()]{7,20}$",
            message = "Phone number is invalid"
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    /**
     * Date of birth of the customer.
     * Used in eligibility rules to validate minimum (21) and maximum (65) age.
     */
    @Column(name = "date_of_birth", nullable = true)
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    // -------------------------------------------------------------------------
    // Financial Profile
    // -------------------------------------------------------------------------

    /**
     * Customer's declared annual income in the system's base currency.
     * Used by the Income eligibility rule.
     */
    @Column(name = "annual_income", nullable = true, precision = 18, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true, message = "Annual income cannot be negative")
    @DecimalMax(value = "99999999.99", message = "Annual income exceeds maximum allowed value")
    @Digits(integer = 16, fraction = 2, message = "Invalid annual income format")
    private BigDecimal annualIncome;

    /**
     * Credit score of the customer from a credit bureau.
     * Typical range: 300–900. Used by CreditScoreRule.
     */
    @Column(name = "credit_score", nullable = true)
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 900, message = "Credit score cannot exceed 900")
    private Integer creditScore;

    /**
     * Customer's current employment type.
     * Stored as string; validated via enum at application layer.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = true, length = 50)
    private EmploymentType employmentType;

    // -------------------------------------------------------------------------
    // Timestamp
    // -------------------------------------------------------------------------

    /**
     * Timestamp when the customer profile was created.
     * Auto-set by DB default (getdate()) and Hibernate on insert.
     * Never updated after creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "datetime default getdate()")
    private LocalDateTime createdAt;

    // -------------------------------------------------------------------------
    // Business Helper Methods
    // -------------------------------------------------------------------------

    /**
     * Calculates the current age of the customer based on date of birth.
     *
     * @return age in years, or -1 if date of birth is not set
     */
    public int getAge() {
        if (this.dateOfBirth == null) {
            return -1;
        }
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Checks whether the customer meets the minimum age requirement for loan eligibility.
     * Minimum age: 21 years.
     *
     * @return true if the customer is 21 or older
     */
    public boolean isMeetingMinimumAge() {
        return getAge() >= 21;
    }

    /**
     * Checks whether the customer is within the maximum age limit for loan eligibility.
     * Maximum age at the time of application: 65 years.
     *
     * @return true if the customer is 65 or younger
     */
    public boolean isMeetingMaximumAge() {
        return getAge() <= 65;
    }

    /**
     * Checks whether the customer is eligible for loan processing based on age.
     *
     * @return true if customer is between 21 and 65 years old
     */
    public boolean isAgeEligible() {
        return isMeetingMinimumAge() && isMeetingMaximumAge();
    }

    /**
     * Checks whether the customer has a strong enough credit score for basic eligibility.
     * Minimum threshold: 650.
     *
     * @return true if credit score is 650 or above
     */
    public boolean hasSufficientCreditScore() {
        return this.creditScore != null && this.creditScore >= 650;
    }

    /**
     * Checks whether the customer is currently employed (eligible employment types).
     *
     * @return true if SALARIED or SELF_EMPLOYED
     */
    public boolean isEmployed() {
        return this.employmentType == EmploymentType.SALARIED
                || this.employmentType == EmploymentType.SELF_EMPLOYED;
    }

    /**
     * Returns the monthly income derived from annual income.
     * Used in DebtToIncomeRule calculations.
     *
     * @return monthly income, or null if annual income is not set
     */
    public BigDecimal getMonthlyIncome() {
        if (this.annualIncome == null) {
            return null;
        }
        return this.annualIncome.divide(BigDecimal.valueOf(12), 2,
                java.math.RoundingMode.HALF_UP);
    }

    /**
     * Checks whether this customer has a linked system user account.
     *
     * @return true if a User account is associated
     */
    public boolean hasUserAccount() {
        return this.user != null;
    }
}
