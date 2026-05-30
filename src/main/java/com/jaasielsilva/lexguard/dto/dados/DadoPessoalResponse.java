package com.jaasielsilva.lexguard.dto.dados;

import com.jaasielsilva.lexguard.model.DataClassification;
import com.jaasielsilva.lexguard.model.LegalBasis;
import com.jaasielsilva.lexguard.model.RiskLevel;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DadoPessoalResponse {

    private Long id;
    private Long titularId;
    private String nomeCampo;
    private String valor;
    private DataClassification classificacao;
    private LegalBasis baseLegal;
    private String finalidade;
    private String localArmazenamento;
    private RiskLevel risco;
    private boolean ativo;
    private String ultimaVezAcessado;
    private Instant createdAt;
    private Instant updatedAt;
}
