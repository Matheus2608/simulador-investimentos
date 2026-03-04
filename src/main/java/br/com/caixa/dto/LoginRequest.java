package br.com.caixa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Credenciais para autenticacao do cliente")
public record LoginRequest(

        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email deve ser valido")
        @Schema(description = "Email cadastrado do cliente", example = "maria@email.com")
        String email,

        @NotBlank(message = "senha e obrigatoria")
        @Schema(description = "Senha do cliente", example = "senha123")
        String senha
) {
}
