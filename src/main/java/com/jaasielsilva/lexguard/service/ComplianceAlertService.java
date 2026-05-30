package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.compliance.ComplianceData;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ComplianceAlertService {

    public List<String> generateAlerts(ComplianceData data) {
        List<String> alertas = new ArrayList<>();

        // Segurança
        if (data.getDadosSensiveisSemProtecao() > 0) {
            alertas.add(String.format(
                    "%d dado(s) sensível(is) sem local de armazenamento declarado",
                    data.getDadosSensiveisSemProtecao()));
        }
        if (data.getUsuariosSemHashSenha() > 0) {
            alertas.add(String.format(
                    "%d usuário(s) com senha sem hash BCrypt adequado",
                    data.getUsuariosSemHashSenha()));
        }

        // Consentimento
        if (data.getConsentimentosExpirados() > 0) {
            alertas.add(String.format(
                    "%d consentimento(s) com data de revogação inconsistente",
                    data.getConsentimentosExpirados()));
        }
        if (data.getTitularesSemConsentimento() > 0) {
            alertas.add(String.format(
                    "%d titular(es) sem consentimento ativo registrado",
                    data.getTitularesSemConsentimento()));
        }

        // Auditoria
        if (data.getLogsUltimos10Dias() < 10) {
            alertas.add(String.format(
                    "Apenas %d log(s) de auditoria nos últimos 10 dias (mínimo recomendado: 10)",
                    data.getLogsUltimos10Dias()));
        }

        // Legal
        if (data.isPoliticaPrivacidadeAusente()) {
            alertas.add("Nenhum consentimento com versão de termo de privacidade definida");
        }

        return alertas;
    }
}
