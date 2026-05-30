package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.titular.TitularRequest;
import com.jaasielsilva.lexguard.dto.titular.TitularResponse;
import com.jaasielsilva.lexguard.dto.titular.TitularSearchPageResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.Titular;
import com.jaasielsilva.lexguard.repository.TitularRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TitularService {

    private final TitularRepository titularRepository;
    private final AuditService auditService;

    public TitularService(TitularRepository titularRepository, AuditService auditService) {
        this.titularRepository = titularRepository;
        this.auditService = auditService;
    }

    @Transactional
    public TitularResponse create(TitularRequest request, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa não informada");
        }
        if (titularRepository.findByCpfAndEmpresaId(request.getCpf(), empresaId).isPresent()) {
            throw new BadRequestException("Titular com CPF informado já existe");
        }
        Titular titular = new Titular();
        titular.setEmpresaId(empresaId);
        titular.setNome(request.getNome());
        titular.setCpf(request.getCpf());
        titular.setEmail(request.getEmail());
        titular.setTelefone(request.getTelefone());
        titular.setClassificacao(request.getClassificacao());
        titular.setAtivo(true);
        titular = titularRepository.save(titular);
        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.CREATE_TITULAR,
                "Titular", "Criado titular: " + titular.getNome(), "Gestão de titular",
                com.jaasielsilva.lexguard.model.LegalBasis.CONSENTIMENTO);
        return mapToResponse(titular);
    }

    public List<TitularResponse> listAll() {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa não informada");
        }
        return titularRepository.findByEmpresaIdAndAtivoTrueAndSoftDeletedFalseOrderByNomeAsc(empresaId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TitularSearchPageResponse search(String query, int page, int size) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa não informada");
        }
        String term = query == null ? "" : query.trim();
        if (term.length() < 2) {
            return new TitularSearchPageResponse(List.of(), 0, page, size, false);
        }
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 0);
        String termDigits = term.replaceAll("\\D", "");
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Titular> result = titularRepository.searchByEmpresa(empresaId, term, termDigits, pageable);
        List<TitularResponse> items = result.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        boolean hasMore = result.getTotalElements() > (long) (safePage + 1) * safeSize;
        return new TitularSearchPageResponse(
                items,
                result.getTotalElements(),
                safePage,
                safeSize,
                hasMore);
    }

    public TitularResponse getById(Long id) {
        Long empresaId = TenantContext.getEmpresaId();
        Titular titular = titularRepository.findById(id)
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Titular não encontrado"));
        return mapToResponse(titular);
    }

    @Transactional
    public TitularResponse update(Long id, TitularRequest request, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Titular titular = titularRepository.findById(id)
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Titular não encontrado"));
        titular.setNome(request.getNome());
        titular.setEmail(request.getEmail());
        titular.setTelefone(request.getTelefone());
        titular.setClassificacao(request.getClassificacao());
        titular = titularRepository.save(titular);
        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.UPDATE_TITULAR,
                "Titular", "Atualizado titular: " + titular.getNome(), "Gestão de titular",
                com.jaasielsilva.lexguard.model.LegalBasis.EXECUCAO_CONTRATO);
        return mapToResponse(titular);
    }

    @Transactional
    public void softDelete(Long id, String usuario) {
        Long empresaId = TenantContext.getEmpresaId();
        Titular titular = titularRepository.findById(id)
                .filter(t -> t.getEmpresaId().equals(empresaId) && t.isAtivo())
                .orElseThrow(() -> new ResourceNotFoundException("Titular não encontrado"));
        titular.setAtivo(false);
        titularRepository.save(titular);
        auditService.logAction(empresaId, usuario, com.jaasielsilva.lexguard.model.AuditLogAction.DELETE_TITULAR,
                "Titular", "Excluído titular: " + titular.getNome(), "Gestão de titular",
                com.jaasielsilva.lexguard.model.LegalBasis.OBRIGACAO_LEGAL);
    }

    private TitularResponse mapToResponse(Titular titular) {
        return new TitularResponse(titular.getId(), titular.getNome(), titular.getCpf(), titular.getEmail(),
                titular.getTelefone(), titular.getClassificacao(), titular.isAtivo(), titular.isSoftDeleted(),
                titular.getCreatedAt(), titular.getUpdatedAt());
    }
}
