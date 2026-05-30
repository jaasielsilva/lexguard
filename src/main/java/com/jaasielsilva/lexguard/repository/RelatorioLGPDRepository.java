package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.RelatorioLGPD;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioLGPDRepository extends JpaRepository<RelatorioLGPD, Long> {

    List<RelatorioLGPD> findByEmpresaId(Long empresaId);
}
