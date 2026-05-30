package com.jaasielsilva.lexguard.dto.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido")
    private String cnpj;

    @NotBlank(message = "E-mail de contato é obrigatório")
    @Email(message = "E-mail inválido")
    private String contatoEmail;
}
