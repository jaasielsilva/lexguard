package com.jaasielsilva.lexguard.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {

    private long totalTitulares;
    private long totalDadosSensiveis;
    private long acessosRecentes;
    private long solicitacoesPendentes;
    private long consentimentosAtivos;
    private String complianceStatus;
}
