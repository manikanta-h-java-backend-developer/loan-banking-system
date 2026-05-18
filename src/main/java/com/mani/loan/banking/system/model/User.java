package com.mani.loan.banking.system.model;

import com.mani.loan.banking.system.constant.UserRoles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "dbo")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", nullable = false, unique = true)
    @NotNull(message = "Username is required")
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @NotNull(message = "Email is required")
    private String email;

    @Column(name = "password", nullable = false)
    @NotNull(message = "Password is required")
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "User role is required")
    private UserRoles userRole;

    @Column(name = "is_active", nullable = true, columnDefinition = "bit default 1")
    @NotNull(message = "User status can be null, defaults to ACTIVE")
    private Boolean userStatus = true; // Default to ACTIVE when a new user is created

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime default getdate()")
    @CreationTimestamp
    private LocalDateTime createdAt;

}
