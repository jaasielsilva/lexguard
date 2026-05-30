package com.jaasielsilva.lexguard.dto.consentimento;

import com.jaasielsilva.lexguard.model.LegalBasis;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConsentimentoRequest {

    @NotNull
    private Long titularId;

    @NotBlank
    private String finalidade;

    @NotNull
    private LegalBasis baseLegal;

    @NotBlank
    private String versaoTermo;
}
