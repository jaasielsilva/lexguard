package com.jaasielsilva.lexguard.dto.dados;

import com.jaasielsilva.lexguard.model.DataClassification;
import com.jaasielsilva.lexguard.model.LegalBasis;
import com.jaasielsilva.lexguard.model.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DadoPessoalRequest {

    @NotNull
    private Long titularId;

    @NotBlank
    private String nomeCampo;

    @NotBlank
    private String valor;

    @NotNull
    private DataClassification classificacao;

    @NotNull
    private LegalBasis baseLegal;

    @NotBlank
    private String finalidade;

    private String localArmazenamento;

    @NotNull
    private RiskLevel risco;
}
