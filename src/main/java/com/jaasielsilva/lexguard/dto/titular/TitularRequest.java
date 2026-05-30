package com.jaasielsilva.lexguard.dto.titular;

import com.jaasielsilva.lexguard.model.DataClassification;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TitularRequest {

    @NotBlank
    private String nome;

    @NotBlank
    @Pattern(
    regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",message = "CPF inválido")
    private String cpf;

    @NotBlank
    @Email
    private String email;

    private String telefone;

    private DataClassification classificacao = DataClassification.PESSOAL;
}
