package com.jaasielsilva.lexguard.dto.solicitacao;

import com.jaasielsilva.lexguard.model.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolicitacaoStatusUpdateRequest {

    @NotNull
    private RequestStatus status;
}
