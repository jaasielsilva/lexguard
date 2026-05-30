package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.Empresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCnpjAndEmpresaId(String cnpj, Long empresaId);

    Optional<Empresa> findByCnpj(String cnpj);
}
