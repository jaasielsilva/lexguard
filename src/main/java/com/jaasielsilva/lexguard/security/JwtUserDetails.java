package com.jaasielsilva.lexguard.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUserDetails implements UserDetails {

    private final String username;
    private final Long empresaId;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(String username, Long empresaId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.empresaId = empresaId;
        this.authorities = authorities;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
