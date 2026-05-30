package com.jaasielsilva.lexguard.dto.auth;

import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private Long empresaId;
    private String message;
    private Set<String> roles = Collections.emptySet();
    private Set<String> permissions = Collections.emptySet();

    public static AuthResponse error(String message) {
        return new AuthResponse("", "", null, message, Collections.emptySet(), Collections.emptySet());
    }

    public static AuthResponse success(
            String accessToken,
            String refreshToken,
            Long empresaId,
            String message,
            Set<String> roles,
            Set<String> permissions) {
        return new AuthResponse(accessToken, refreshToken, empresaId, message, roles, permissions);
    }
}
