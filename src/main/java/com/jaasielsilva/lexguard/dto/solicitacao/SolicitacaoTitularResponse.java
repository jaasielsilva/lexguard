package com.jaasielsilva.lexguard.dto.solicitacao;

import com.jaasielsilva.lexguard.model.RequestStatus;
import com.jaasielsilva.lexguard.model.RightRequestType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoTitularResponse {

    private Long id;
    private Long titularId;
    private String titularNome;
    private RightRequestType tipo;
    private String descricao;
    private RequestStatus status;
    private String resposta;
    private Instant solicitadoEm;
    private Instant atendidoEm;
    private String concluidoPor;
}
