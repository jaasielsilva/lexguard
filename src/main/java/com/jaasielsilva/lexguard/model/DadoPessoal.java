package com.jaasielsilva.lexguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dados_pessoais")
@Filter(name = "tenantFilter", condition = "empresa_id = :empresaId")
public class DadoPessoal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", nullable = false)
    private Titular titular;

    @Column(name = "nome_campo", nullable = false, length = 120)
    private String nomeCampo;

    @Column(name = "valor", nullable = false, length = 500)
    private String valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao", nullable = false)
    private DataClassification classificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_legal", nullable = false)
    private LegalBasis baseLegal;

    @Column(name = "finalidade", nullable = false, length = 200)
    private String finalidade;

    @Column(name = "local_armazenamento", length = 200)
    private String localArmazenamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "risco", nullable = false)
    private RiskLevel risco = RiskLevel.MEDIO;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "ultima_vez_acessado")
    private String ultimaVezAcessado;
}
