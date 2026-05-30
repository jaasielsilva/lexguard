package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.compliance.ComplianceData;
import com.jaasielsilva.lexguard.dto.compliance.ComplianceScoreResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ComplianceScoreService {

    private final ComplianceDataService complianceDataService;
    private final ComplianceAlertService complianceAlertService;

    public ComplianceScoreService(
            ComplianceDataService complianceDataService,
            ComplianceAlertService complianceAlertService) {
        this.complianceDataService = complianceDataService;
        this.complianceAlertService = complianceAlertService;
    }

    public ComplianceScoreResponse calculate() {
        ComplianceData data = complianceDataService.collect();

        int riscoSeguranca = calcularRiscoSeguranca(data);
        int riscoConsentimento = calcularRiscoConsentimento(data);
        int riscoAuditoria = calcularRiscoAuditoria(data);
        int riscoLegal = calcularRiscoLegal(data);

        int totalRisco = riscoSeguranca + riscoConsentimento + riscoAuditoria + riscoLegal;
        int score = Math.max(0, 100 - totalRisco);

        String status = resolveStatus(score);
        List<String> alertas = complianceAlertService.generateAlerts(data);

        return new ComplianceScoreResponse(score, status, alertas,
                riscoSeguranca, riscoConsentimento, riscoAuditoria, riscoLegal);
    }

    // --- Regras de risco ---

    private int calcularRiscoSeguranca(ComplianceData data) {
        int risco = 0;
        if (data.getDadosSensiveisSemProtecao() > 0)
            risco += 30;
        if (data.getUsuariosSemHashSenha() > 0)
            risco += 20;
        return risco;
    }

    private int calcularRiscoConsentimento(ComplianceData data) {
        int risco = 0;
        if (data.getConsentimentosExpirados() > 0)
            risco += 25;
        if (data.getTitularesSemConsentimento() > 0)
            risco += 30;
        return risco;
    }

    private int calcularRiscoAuditoria(ComplianceData data) {
        return data.getLogsUltimos10Dias() < 10 ? 20 : 0;
    }

    private int calcularRiscoLegal(ComplianceData data) {
        return data.isPoliticaPrivacidadeAusente() ? 25 : 0;
    }

    private String resolveStatus(int score) {
        if (score >= 80)
            return "BAIXO RISCO";
        if (score >= 50)
            return "ATENÇÃO";
        return "CRÍTICO";
    }
}
