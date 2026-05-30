package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.DadoPessoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DadoPessoalRepository extends JpaRepository<DadoPessoal, Long> {

    List<DadoPessoal> findByEmpresaIdAndAtivoTrue(Long empresaId);

    List<DadoPessoal> findByTitularIdAndEmpresaId(Long titularId, Long empresaId);
}
