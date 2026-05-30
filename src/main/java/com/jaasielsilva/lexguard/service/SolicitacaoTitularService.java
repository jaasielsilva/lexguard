package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.solicitacao.SolicitacaoTitularRequest;
import com.jaasielsilva.lexguard.dto.solicitacao.SolicitacaoTitularResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.RequestStatus;
import com.jaasielsilva.lexguard.model.SolicitacaoTitular;
import com.jaasielsilva.lexguard.model.Titular;
import com.jaasielsilva.lexguard.repository.SolicitacaoTitularRepository;
import com.jaasielsilva.lexguard.repository.TitularRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitacaoTitularService {

    private final SolicitacaoTitularRepository solicitacaoRepository;
    private final TitularRepository titularRepository;
    private final AuditService auditService;

    public SolicitacaoTitularService(SolicitacaoTitularRepository solicitacaoRepository,
            TitularRepository titularRepository, AuditService auditService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.titularRepository = titularRepository;
        this.auditService = auditService;
    }

    @Transactional
    public SolicitacaoTitularResponse createRequest(SolicitacaoTitularRequest request, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Titular titular = titularRepository.findById(request.getTitularId())
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Titular não encontrado"));

        SolicitacaoTitular solicitacao = new SolicitacaoTitular();
        solicitacao.setEmpresaId(empresaId);
        solicitacao.setTitular(titular);
        solicitacao.setTipo(request.getTipo());
        solicitacao.setDescricao(request.getDescricao());
        solicitacao.setStatus(RequestStatus.PENDENTE);
        solicitacao.setSolicitadoEm(Instant.now());
        solicitacao = solicitacaoRepository.save(solicitacao);

        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.REQUEST_SUBMITTED,
                "SolicitacaoTitular", "Solicitação registrada para titular: " + titular.getNome(),
                "Gestão de solicitações", com.jaasielsilva.lexguard.model.LegalBasis.OBRIGACAO_LEGAL);
        return mapToResponse(solicitacao);
    }

    public Set<SolicitacaoTitularResponse> listAll() {
        Long empresaId = TenantContext.getEmpresaId();
        return solicitacaoRepository.findAll().stream()
                .filter(s -> s.getEmpresaId().equals(empresaId))
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
    }

    @Transactional
    public SolicitacaoTitularResponse respondToRequest(Long id, String resposta, String concluidoPor,
            RequestStatus status, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        SolicitacaoTitular solicitacao = solicitacaoRepository.findById(id)
                .filter(s -> s.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        if (solicitacao.getStatus() != RequestStatus.PENDENTE) {
            throw new BadRequestException("Solicitação já foi atendida");
        }
        solicitacao.setResposta(resposta);
        solicitacao.setAtendidoEm(Instant.now());
        solicitacao.setConcluidoPor(concluidoPor);
        solicitacao.setStatus(status);
        solicitacao = solicitacaoRepository.save(solicitacao);

        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.REQUEST_HANDLED,
                "SolicitacaoTitular", "Solicitação de titular atendida: " + solicitacao.getId(),
                "Gestão de solicitações", com.jaasielsilva.lexguard.model.LegalBasis.OBRIGACAO_LEGAL);
        return mapToResponse(solicitacao);
    }

    private SolicitacaoTitularResponse mapToResponse(SolicitacaoTitular solicitacao) {
        return new SolicitacaoTitularResponse(
                solicitacao.getId(),
                solicitacao.getTitular().getId(),
                solicitacao.getTipo(),
                solicitacao.getDescricao(),
                solicitacao.getStatus(),
                solicitacao.getResposta(),
                solicitacao.getSolicitadoEm(),
                solicitacao.getAtendidoEm(),
                solicitacao.getConcluidoPor());
    }
}
