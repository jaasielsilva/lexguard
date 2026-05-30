package com.jaasielsilva.lexguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "consentimentos")
@Filter(name = "tenantFilter", condition = "empresa_id = :empresaId")
public class Consentimento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", nullable = false)
    private Titular titular;

    @Column(name = "finalidade", nullable = false, length = 200)
    private String finalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_legal", nullable = false)
    private LegalBasis baseLegal;

    @Column(name = "versao_termo", nullable = false, length = 50)
    private String versaoTermo;

    @Column(name = "aceite_em", nullable = false)
    private Instant aceiteEm;

    @Column(name = "revogado_em")
    private Instant revogadoEm;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
