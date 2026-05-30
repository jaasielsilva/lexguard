package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.Titular;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Long> {

    List<Titular> findByEmpresaIdAndSoftDeletedFalse(Long empresaId);

    Optional<Titular> findByIdAndEmpresaId(Long id, Long empresaId);

    Optional<Titular> findByCpfAndEmpresaId(String cpf, Long empresaId);
}
