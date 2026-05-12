package com.mani.loan.banking.system.model;

import com.mani.loan.banking.system.constant.AuditLogActions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audit_logs", schema = "dbo")
public class AuditLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long auditLogId;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditLogActions action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_audit_logs_performed_by"),
            nullable = true)
    private User performedBy;

    @Column(name = "old_value", nullable = true)
    private String oldLogValue;

    @Column(name = "new_value", nullable = true)
    private String newLogValue;

    @Column(name = "timestamp", nullable = true,
            columnDefinition = "datetime default getdate()")
    private LocalDateTime logTimestamp;

}
