package br.com.caixa.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resposta de autenticacao contendo o token JWT")
public record LoginResponse(

        @Schema(description = "Token JWT para uso no header Authorization (Bearer <token>). Valido por 1 hora.",
                example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzaW11bGFkb3ItaW52ZXN0aW1lbnRvcyIsInN1YiI6IjEiLCJ...")
        String token
) {
}
