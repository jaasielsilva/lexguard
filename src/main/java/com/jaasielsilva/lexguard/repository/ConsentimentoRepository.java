package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.Consentimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentimentoRepository extends JpaRepository<Consentimento, Long> {

    List<Consentimento> findByEmpresaIdAndAtivoTrue(Long empresaId);

    List<Consentimento> findByTitularIdAndEmpresaId(Long titularId, Long empresaId);
}
