package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.SolicitacaoTitular;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoTitularRepository extends JpaRepository<SolicitacaoTitular, Long> {

    List<SolicitacaoTitular> findByEmpresaId(Long empresaId);

    List<SolicitacaoTitular> findByTitularIdAndEmpresaId(Long titularId, Long empresaId);
}
