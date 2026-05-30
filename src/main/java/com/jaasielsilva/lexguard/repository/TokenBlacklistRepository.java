package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.TokenBlacklist;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    Optional<TokenBlacklist> findByTokenAndEmpresaId(String token, Long empresaId);
}
