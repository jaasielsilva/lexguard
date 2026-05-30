package com.jaasielsilva.lexguard.dto.solicitacao;

import com.jaasielsilva.lexguard.model.RightRequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolicitacaoTitularRequest {

    @NotNull
    private Long titularId;

    @NotNull
    private RightRequestType tipo;

    @NotBlank
    private String descricao;
}
