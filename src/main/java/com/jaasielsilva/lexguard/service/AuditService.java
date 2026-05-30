package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.model.AuditLog;
import com.jaasielsilva.lexguard.model.AuditLogAction;
import com.jaasielsilva.lexguard.model.DataAccessLog;
import com.jaasielsilva.lexguard.model.LegalBasis;
import com.jaasielsilva.lexguard.repository.AuditLogRepository;
import com.jaasielsilva.lexguard.repository.DataAccessLogRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final DataAccessLogRepository dataAccessLogRepository;

    public AuditService(AuditLogRepository auditLogRepository, DataAccessLogRepository dataAccessLogRepository) {
        this.auditLogRepository = auditLogRepository;
        this.dataAccessLogRepository = dataAccessLogRepository;
    }

    @Transactional
    public AuditLog logAction(Long empresaId, String usuario, AuditLogAction action, String resource, String descricao, String finalidade, LegalBasis baseLegal) {
        AuditLog log = new AuditLog();
        log.setEmpresaId(empresaId);
        log.setUsuario(usuario);
        log.setAction(action);
        log.setResource(resource);
        log.setDescricao(descricao);
        log.setFinalidade(finalidade);
        log.setBaseLegal(baseLegal);
        log.setTimestamp(Instant.now());
        return auditLogRepository.save(log);
    }

    @Transactional
    public DataAccessLog logDataAccess(Long empresaId, String usuario, String titular, String dado, String finalidade, LegalBasis baseLegal) {
        DataAccessLog log = new DataAccessLog();
        log.setEmpresaId(empresaId);
        log.setUsuario(usuario);
        log.setTitular(titular);
        log.setDado(dado);
        log.setFinalidade(finalidade);
        log.setBaseLegal(baseLegal);
        log.setAcessadoEm(Instant.now());
        return dataAccessLogRepository.save(log);
    }
}
