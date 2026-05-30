package com.jaasielsilva.lexguard.dto.user;

import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String nome;
    private String email;
    private boolean ativo;
    private Set<String> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
