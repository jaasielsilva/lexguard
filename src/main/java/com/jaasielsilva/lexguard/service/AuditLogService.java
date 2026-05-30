package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.audit.AuditLogResponse;
import com.jaasielsilva.lexguard.dto.audit.AuditLogSearchPageResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.model.AuditLog;
import com.jaasielsilva.lexguard.model.AuditLogAction;
import com.jaasielsilva.lexguard.repository.AuditLogRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogSearchPageResponse search(String query, String actionGroup, int page, int size) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa não informada");
        }

        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 0);
        String term = query == null ? "" : query.trim();

        List<AuditLogAction> actions = resolveActionGroup(actionGroup);
        boolean filterByAction = actions != null;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<AuditLog> result;
        if (filterByAction) {
            result = auditLogRepository.searchByEmpresaTermAndActions(empresaId, term, actions, pageable);
        } else if (!term.isEmpty()) {
            result = auditLogRepository.searchByEmpresaTerm(empresaId, term, pageable);
        } else {
            result = auditLogRepository.findByEmpresaIdOrderByTimestampDesc(empresaId, pageable);
        }

        List<AuditLogResponse> items = result.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        boolean hasMore = result.getTotalElements() > (long) (safePage + 1) * safeSize;
        return new AuditLogSearchPageResponse(
                items,
                result.getTotalElements(),
                safePage,
                safeSize,
                hasMore);
    }

  private List<AuditLogAction> resolveActionGroup(String actionGroup) {
        if (actionGroup == null || actionGroup.isBlank()) {
            return null;
        }
        return switch (actionGroup.toUpperCase()) {
            case "AUTH" -> List.of(AuditLogAction.LOGIN, AuditLogAction.LOGOUT);
            case "TITULAR" -> List.of(
                    AuditLogAction.CREATE_TITULAR,
                    AuditLogAction.UPDATE_TITULAR,
                    AuditLogAction.DELETE_TITULAR);
            case "CONSENT" -> List.of(AuditLogAction.REGISTER_CONSENT, AuditLogAction.REVOKE_CONSENT);
            case "DATA" -> List.of(AuditLogAction.DATA_ACCESS, AuditLogAction.DATA_EXPORT);
            case "REQUEST" -> List.of(AuditLogAction.REQUEST_SUBMITTED, AuditLogAction.REQUEST_HANDLED);
            case "REPORT" -> List.of(AuditLogAction.REPORT_GENERATED);
            default -> throw new BadRequestException("Grupo de ação inválido: " + actionGroup);
        };
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getAction(),
                log.getUsuario(),
                log.getResource(),
                log.getDescricao(),
                log.getFinalidade(),
                log.getBaseLegal(),
                log.getTimestamp());
    }
}
