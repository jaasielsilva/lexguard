package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.AuditLog;
import com.jaasielsilva.lexguard.model.AuditLogAction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByEmpresaIdOrderByTimestampDesc(Long empresaId, Pageable pageable);

    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.empresaId = :empresaId
              AND (
                :term = ''
                OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :term, '%'))
                OR LOWER(COALESCE(a.descricao, '')) LIKE LOWER(CONCAT('%', :term, '%'))
                OR LOWER(COALESCE(a.resource, '')) LIKE LOWER(CONCAT('%', :term, '%'))
              )
            ORDER BY a.timestamp DESC
            """)
    Page<AuditLog> searchByEmpresaTerm(
            @Param("empresaId") Long empresaId,
            @Param("term") String term,
            Pageable pageable);

    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.empresaId = :empresaId
              AND a.action IN :actions
              AND (
                :term = ''
                OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :term, '%'))
                OR LOWER(COALESCE(a.descricao, '')) LIKE LOWER(CONCAT('%', :term, '%'))
                OR LOWER(COALESCE(a.resource, '')) LIKE LOWER(CONCAT('%', :term, '%'))
              )
            ORDER BY a.timestamp DESC
            """)
    Page<AuditLog> searchByEmpresaTermAndActions(
            @Param("empresaId") Long empresaId,
            @Param("term") String term,
            @Param("actions") List<AuditLogAction> actions,
            Pageable pageable);
}
