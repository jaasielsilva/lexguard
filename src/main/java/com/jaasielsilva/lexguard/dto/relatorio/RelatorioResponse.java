package com.jaasielsilva.lexguard.dto.relatorio;

import com.jaasielsilva.lexguard.model.ReportType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private ReportType tipo;
    private String conteudo;
    private Instant geradoEm;
    private String geradoPor;
}
