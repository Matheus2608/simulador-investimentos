package br.com.caixa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Dados para registro de novo cliente")
public record RegistroRequest(

        @NotBlank(message = "nome e obrigatorio")
        @Schema(description = "Nome completo do cliente", example = "Maria Silva")
        String nome,

        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email deve ser valido")
        @Schema(description = "Email do cliente (deve ser unico no sistema)", example = "maria@email.com")
        String email,

        @NotBlank(message = "senha e obrigatoria")
        @Size(min = 6, message = "senha deve ter no minimo 6 caracteres")
        @Schema(description = "Senha do cliente (minimo 6 caracteres)", example = "senha123", minLength = 6)
        String senha
) {
}
