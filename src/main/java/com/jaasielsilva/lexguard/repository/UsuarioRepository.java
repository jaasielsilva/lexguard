package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameAndEmpresaId(String username, Long empresaId);

    Optional<Usuario> findByEmailAndEmpresaId(String email, Long empresaId);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByEmpresaId(Long empresaId);
}
