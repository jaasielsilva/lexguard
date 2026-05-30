package com.jaasielsilva.lexguard.dto.compliance;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceScoreResponse {

    private int score;
    private String status;
    private List<String> alertas;
    private int riscoSeguranca;
    private int riscoConsentimento;
    private int riscoAuditoria;
    private int riscoLegal;
}
