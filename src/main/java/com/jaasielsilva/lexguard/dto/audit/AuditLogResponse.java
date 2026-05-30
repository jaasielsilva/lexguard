package com.jaasielsilva.lexguard.dto.audit;

import com.jaasielsilva.lexguard.model.AuditLogAction;
import com.jaasielsilva.lexguard.model.LegalBasis;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private AuditLogAction action;
    private String usuario;
    private String resource;
    private String descricao;
    private String finalidade;
    private LegalBasis baseLegal;
    private Instant timestamp;
}
