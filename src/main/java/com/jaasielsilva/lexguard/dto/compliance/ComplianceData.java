package com.jaasielsilva.lexguard.dto.compliance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ComplianceData {

    private final long totalDadosSensiveis;
    private final long dadosSensiveisSemProtecao;
    private final long usuariosSemHashSenha;
    private final long consentimentosExpirados;
    private final long titularesSemConsentimento;
    private final long logsUltimos10Dias;
    private final boolean politicaPrivacidadeAusente;
}
