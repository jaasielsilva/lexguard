package com.jaasielsilva.lexguard.dto.titular;

import com.jaasielsilva.lexguard.model.DataClassification;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TitularResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private DataClassification classificacao;
    private boolean ativo;
    private boolean softDeleted;
    private Instant createdAt;
    private Instant updatedAt;
}
