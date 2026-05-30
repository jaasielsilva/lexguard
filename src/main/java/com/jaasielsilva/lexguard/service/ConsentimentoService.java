package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.consentimento.ConsentimentoRequest;
import com.jaasielsilva.lexguard.dto.consentimento.ConsentimentoResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.Consentimento;
import com.jaasielsilva.lexguard.model.LegalBasis;
import com.jaasielsilva.lexguard.model.Titular;
import com.jaasielsilva.lexguard.repository.ConsentimentoRepository;
import com.jaasielsilva.lexguard.repository.TitularRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsentimentoService {

    private final ConsentimentoRepository consentimentoRepository;
    private final TitularRepository titularRepository;
    private final AuditService auditService;

    public ConsentimentoService(ConsentimentoRepository consentimentoRepository, TitularRepository titularRepository,
            AuditService auditService) {
        this.consentimentoRepository = consentimentoRepository;
        this.titularRepository = titularRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ConsentimentoResponse registerConsent(ConsentimentoRequest request, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Titular titular = titularRepository.findById(request.getTitularId())
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Titular não encontrado"));

        Consentimento consentimento = new Consentimento();
        consentimento.setEmpresaId(empresaId);
        consentimento.setTitular(titular);
        consentimento.setFinalidade(request.getFinalidade());
        consentimento.setBaseLegal(request.getBaseLegal());
        consentimento.setVersaoTermo(request.getVersaoTermo());
        consentimento.setAceiteEm(Instant.now());
        consentimento.setRevogadoEm(null);
        consentimento.setAtivo(true);
        consentimento = consentimentoRepository.save(consentimento);

        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.REGISTER_CONSENT,
                "Consentimento", "Consentimento registrado para titular: " + titular.getNome(),
                "Gestão de consentimento", LegalBasis.CONSENTIMENTO);
        return mapToResponse(consentimento);
    }

    public Set<ConsentimentoResponse> listByTitular(Long titularId) {
        Long empresaId = TenantContext.getEmpresaId();
        return consentimentoRepository.findAll().stream()
                .filter(c -> c.getEmpresaId().equals(empresaId) && c.getTitular().getId().equals(titularId))
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ConsentimentoResponse revokeConsent(Long id, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Consentimento consentimento = consentimentoRepository.findById(id)
                .filter(c -> c.getEmpresaId().equals(empresaId) && c.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Consentimento não encontrado"));
        consentimento.setRevogadoEm(Instant.now());
        consentimento.setAtivo(false);
        consentimento = consentimentoRepository.save(consentimento);
        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.REVOKE_CONSENT,
                "Consentimento", "Consentimento revogado para titular: " + consentimento.getTitular().getNome(),
                "Gestão de consentimento", LegalBasis.CONSENTIMENTO);
        return mapToResponse(consentimento);
    }

    private ConsentimentoResponse mapToResponse(Consentimento consentimento) {
        return new ConsentimentoResponse(
                consentimento.getId(),
                consentimento.getTitular().getId(),
                consentimento.getFinalidade(),
                consentimento.getBaseLegal(),
                consentimento.getVersaoTermo(),
                consentimento.getAceiteEm(),
                consentimento.getRevogadoEm(),
                consentimento.isAtivo());
    }
}
