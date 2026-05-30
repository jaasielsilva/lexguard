package com.jaasielsilva.lexguard.dto.role;

import com.jaasielsilva.lexguard.model.Permission;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private Set<Permission> permissions;
    private boolean assignable;
    private boolean systemRole;
}
