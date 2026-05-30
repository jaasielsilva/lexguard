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
@Table(name = "data_access_logs")
@Filter(name = "tenantFilter", condition = "empresa_id = :empresaId")
public class DataAccessLog extends BaseEntity {

    @Column(name = "usuario", length = 120)
    private String usuario;

    @Column(name = "titular", length = 120)
    private String titular;

    @Column(name = "dado", length = 200)
    private String dado;

    @Column(name = "finalidade", length = 200)
    private String finalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_legal", length = 80)
    private LegalBasis baseLegal;

    @Column(name = "acessado_em", nullable = false)
    private Instant acessadoEm = Instant.now();
}
