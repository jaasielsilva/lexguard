package com.jaasielsilva.lexguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "titulares")
@Filter(name = "tenantFilter", condition = "empresa_id = :empresaId")
public class Titular extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 180)
    private String nome;

    @Column(name = "cpf", nullable = false, unique = true, length = 20)
    private String cpf;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefone", length = 40)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao", nullable = false)
    private DataClassification classificacao = DataClassification.PESSOAL;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "soft_deleted", nullable = false)
    private boolean softDeleted = false;
}
