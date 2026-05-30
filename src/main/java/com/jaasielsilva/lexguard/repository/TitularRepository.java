package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.Titular;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Long> {

    List<Titular> findByEmpresaIdAndSoftDeletedFalse(Long empresaId);

    List<Titular> findByEmpresaIdAndAtivoTrueAndSoftDeletedFalseOrderByNomeAsc(Long empresaId);

    Optional<Titular> findByIdAndEmpresaId(Long id, Long empresaId);

    Optional<Titular> findByCpfAndEmpresaId(String cpf, Long empresaId);

    @Query("""
            SELECT t FROM Titular t
            WHERE t.empresaId = :empresaId
              AND t.ativo = true
              AND t.softDeleted = false
              AND (
                LOWER(t.nome) LIKE LOWER(CONCAT('%', :term, '%'))
                OR t.cpf LIKE CONCAT('%', :termDigits, '%')
                OR LOWER(t.email) LIKE LOWER(CONCAT('%', :term, '%'))
              )
            ORDER BY t.nome ASC
            """)
    Page<Titular> searchByEmpresa(
            @Param("empresaId") Long empresaId,
            @Param("term") String term,
            @Param("termDigits") String termDigits,
            Pageable pageable);
}
