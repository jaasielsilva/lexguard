package com.jaasielsilva.lexguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "token_blacklist")
@Filter(name = "tenantFilter", condition = "empresa_id = :empresaId")
public class TokenBlacklist extends BaseEntity {

    @Column(name = "token", nullable = false, length = 500, unique = true)
    private String token;

    @Column(name = "revoked_at", nullable = false)
    private Instant revokedAt = Instant.now();
}
