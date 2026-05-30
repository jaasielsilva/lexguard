package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.relatorio.RelatorioResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.ReportType;
import com.jaasielsilva.lexguard.model.RelatorioLGPD;
import com.jaasielsilva.lexguard.repository.RelatorioLGPDRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {

    private final RelatorioLGPDRepository relatorioRepository;
    private final AuditService auditService;

    public RelatorioService(RelatorioLGPDRepository relatorioRepository, AuditService auditService) {
        this.relatorioRepository = relatorioRepository;
        this.auditService = auditService;
    }

    @Transactional
    public RelatorioResponse generateReport(String titulo, String descricao, ReportType tipo, String conteudo,
            String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa não informada");
        }
        RelatorioLGPD relatorio = new RelatorioLGPD();
        relatorio.setEmpresaId(empresaId);
        relatorio.setTitulo(titulo);
        relatorio.setDescricao(descricao);
        relatorio.setTipo(tipo);
        relatorio.setConteudo(conteudo);
        relatorio.setGeradoEm(Instant.now());
        relatorio.setGeradoPor(usuario);
        relatorio = relatorioRepository.save(relatorio);
        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.REPORT_GENERATED,
                "RelatorioLGPD", "Relatório gerado: " + titulo, "Geração de relatórios",
                com.jaasielsilva.lexguard.model.LegalBasis.OBRIGACAO_LEGAL);
        return mapToResponse(relatorio);
    }

    public Set<RelatorioResponse> listReports() {
        Long empresaId = TenantContext.getEmpresaId();
        return relatorioRepository.findAll().stream()
                .filter(r -> r.getEmpresaId().equals(empresaId))
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
    }

    public RelatorioResponse getReport(Long id) {
        Long empresaId = TenantContext.getEmpresaId();
        RelatorioLGPD relatorio = relatorioRepository.findById(id)
                .filter(r -> r.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Relatório não encontrado"));
        return mapToResponse(relatorio);
    }

    private RelatorioResponse mapToResponse(RelatorioLGPD relatorio) {
        return new RelatorioResponse(
                relatorio.getId(),
                relatorio.getTitulo(),
                relatorio.getDescricao(),
                relatorio.getTipo(),
                relatorio.getConteudo(),
                relatorio.getGeradoEm(),
                relatorio.getGeradoPor());
    }
}
