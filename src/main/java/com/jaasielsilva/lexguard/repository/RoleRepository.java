package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNameAndEmpresaId(String name, Long empresaId);

    List<Role> findAllByEmpresaIdOrderByNameAsc(Long empresaId);
}
