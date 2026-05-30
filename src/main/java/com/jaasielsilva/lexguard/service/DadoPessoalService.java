package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.dados.DadoPessoalRequest;
import com.jaasielsilva.lexguard.dto.dados.DadoPessoalResponse;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.DadoPessoal;
import com.jaasielsilva.lexguard.model.Titular;
import com.jaasielsilva.lexguard.repository.DadoPessoalRepository;
import com.jaasielsilva.lexguard.repository.TitularRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DadoPessoalService {

    private final DadoPessoalRepository dadoPessoalRepository;
    private final TitularRepository titularRepository;
    private final AuditService auditService;

    public DadoPessoalService(DadoPessoalRepository dadoPessoalRepository, TitularRepository titularRepository,
            AuditService auditService) {
        this.dadoPessoalRepository = dadoPessoalRepository;
        this.titularRepository = titularRepository;
        this.auditService = auditService;
    }

    @Transactional
    public DadoPessoalResponse create(DadoPessoalRequest request, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Titular titular = titularRepository.findById(request.getTitularId())
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Titular não encontrado"));

        DadoPessoal dado = new DadoPessoal();
        dado.setEmpresaId(empresaId);
        dado.setTitular(titular);
        dado.setNomeCampo(request.getNomeCampo());
        dado.setValor(request.getValor());
        dado.setClassificacao(request.getClassificacao());
        dado.setBaseLegal(request.getBaseLegal());
        dado.setFinalidade(request.getFinalidade());
        dado.setLocalArmazenamento(request.getLocalArmazenamento());
        dado.setRisco(request.getRisco());
        dado.setAtivo(true);
        dado = dadoPessoalRepository.save(dado);

        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.DATA_ACCESS,
                "DadoPessoal", "Dado pessoal criado para titular: " + titular.getNome(), "Gerenciamento de dados",
                com.jaasielsilva.lexguard.model.LegalBasis.CONSENTIMENTO);
        return mapToResponse(dado);
    }

    public Set<DadoPessoalResponse> listByTitular(Long titularId, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Set<DadoPessoalResponse> dados = dadoPessoalRepository.findAll().stream()
                .filter(d -> d.getEmpresaId().equals(empresaId) && d.getTitular().getId().equals(titularId)
                        && d.isAtivo())
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
        auditService.logDataAccess(empresaId, usuario, "titularId=" + titularId, "dados pessoais",
                "Consulta de dados pessoais", com.jaasielsilva.lexguard.model.LegalBasis.OBRIGACAO_LEGAL);
        return dados;
    }

    private DadoPessoalResponse mapToResponse(DadoPessoal dado) {
        return new DadoPessoalResponse(
                dado.getId(),
                dado.getTitular().getId(),
                dado.getNomeCampo(),
                dado.getValor(),
                dado.getClassificacao(),
                dado.getBaseLegal(),
                dado.getFinalidade(),
                dado.getLocalArmazenamento(),
                dado.getRisco(),
                dado.isAtivo(),
                dado.getUltimaVezAcessado(),
                dado.getCreatedAt(),
                dado.getUpdatedAt());
    }
}
