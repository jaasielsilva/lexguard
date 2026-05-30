package com.jaasielsilva.lexguard.dto.empresa;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long id;
    private String nome;
    private String cnpj;
    private String contatoEmail;
    private boolean ativo;
    private Instant createdAt;
    private Instant updatedAt;
}
