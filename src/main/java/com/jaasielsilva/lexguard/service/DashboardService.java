package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.dashboard.DashboardMetricsResponse;
import com.jaasielsilva.lexguard.model.DataClassification;
import com.jaasielsilva.lexguard.model.RequestStatus;
import com.jaasielsilva.lexguard.repository.ConsentimentoRepository;
import com.jaasielsilva.lexguard.repository.DadoPessoalRepository;
import com.jaasielsilva.lexguard.repository.DataAccessLogRepository;
import com.jaasielsilva.lexguard.repository.SolicitacaoTitularRepository;
import com.jaasielsilva.lexguard.repository.TitularRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final TitularRepository titularRepository;
    private final DadoPessoalRepository dadoPessoalRepository;
    private final DataAccessLogRepository dataAccessLogRepository;
    private final SolicitacaoTitularRepository solicitacaoTitularRepository;
    private final ConsentimentoRepository consentimentoRepository;

    public DashboardService(TitularRepository titularRepository,
            DadoPessoalRepository dadoPessoalRepository,
            DataAccessLogRepository dataAccessLogRepository,
            SolicitacaoTitularRepository solicitacaoTitularRepository,
            ConsentimentoRepository consentimentoRepository) {
        this.titularRepository = titularRepository;
        this.dadoPessoalRepository = dadoPessoalRepository;
        this.dataAccessLogRepository = dataAccessLogRepository;
        this.solicitacaoTitularRepository = solicitacaoTitularRepository;
        this.consentimentoRepository = consentimentoRepository;
    }

    public DashboardMetricsResponse getMetrics() {
        Long empresaId = TenantContext.getEmpresaId();
        long totalTitulares = titularRepository.findAll().stream()
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .count();
        long totalDadosSensiveis = dadoPessoalRepository.findAll().stream()
                .filter(d -> d.getEmpresaId().equals(empresaId) && d.getClassificacao() == DataClassification.SENSIVEL
                        && d.isAtivo())
                .count();
        long acessosRecentes = dataAccessLogRepository.findAll().stream()
                .filter(log -> log.getEmpresaId().equals(empresaId))
                .count();
        long solicitacoesPendentes = solicitacaoTitularRepository.findAll().stream()
                .filter(s -> s.getEmpresaId().equals(empresaId) && s.getStatus() == RequestStatus.PENDENTE)
                .count();
        long consentimentosAtivos = consentimentoRepository.findAll().stream()
                .filter(c -> c.getEmpresaId().equals(empresaId) && c.isAtivo())
                .count();
        String complianceStatus = consentimentosAtivos > 0 && totalTitulares > 0 ? "SUFICIENTE" : "ATENÇÃO";

        return new DashboardMetricsResponse(totalTitulares, totalDadosSensiveis, acessosRecentes, solicitacoesPendentes,
                consentimentosAtivos, complianceStatus);
    }
}
