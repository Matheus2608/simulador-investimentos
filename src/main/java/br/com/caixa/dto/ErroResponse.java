package br.com.caixa.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resposta padrao de erro da API")
public record ErroResponse(

        @Schema(description = "Codigo HTTP do erro", example = "400")
        int status,

        @Schema(description = "Mensagem descritiva do erro", example = "Dados invalidos na requisicao")
        String mensagem,

        @Schema(description = "Lista detalhada de erros de validacao (quando aplicavel)",
                example = "[\"valor deve ser positivo\", \"senha deve ser ter no minimo 6 caracteres\"]")
        List<String> detalhes,

        @Schema(description = "Data e hora em que o erro ocorreu", example = "2026-03-04T10:30:00")
        LocalDateTime timestamp
) {

    public ErroResponse(int status, String mensagem) {
        this(status, mensagem, List.of(), LocalDateTime.now());
    }

    public ErroResponse(int status, String mensagem, List<String> detalhes) {
        this(status, mensagem, detalhes, LocalDateTime.now());
    }
}
