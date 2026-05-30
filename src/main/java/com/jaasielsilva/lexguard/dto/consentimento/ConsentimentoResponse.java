package com.jaasielsilva.lexguard.dto.consentimento;

import com.jaasielsilva.lexguard.model.LegalBasis;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimentoResponse {

    private Long id;
    private Long titularId;
    private String finalidade;
    private LegalBasis baseLegal;
    private String versaoTermo;
    private Instant aceiteEm;
    private Instant revogadoEm;
    private boolean ativo;
}
