package com.jaasielsilva.lexguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
@Filter(name = "tenantFilter", condition = "empresa_id = :empresaId")
public class AuditLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditLogAction action;

    @Column(name = "usuario", length = 120)
    private String usuario;

    @Column(name = "resource", length = 200)
    private String resource;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "finalidade", length = 200)
    private String finalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_legal", length = 80)
    private LegalBasis baseLegal;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp = Instant.now();
}
