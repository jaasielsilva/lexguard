package com.jaasielsilva.lexguard.dto.role;

import com.jaasielsilva.lexguard.model.Permission;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RolePermissionsUpdateRequest {

    @NotEmpty
    private Set<Permission> permissions;
}
