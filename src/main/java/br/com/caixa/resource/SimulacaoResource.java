package br.com.caixa.resource;

import br.com.caixa.application.SimulacaoService;
import br.com.caixa.domain.Simulacao;
import br.com.caixa.dto.AgregacaoResponse;
import br.com.caixa.dto.ErroResponse;
import br.com.caixa.dto.HistoricoSimulacaoResponse;
import br.com.caixa.dto.SimulacaoRequest;
import br.com.caixa.dto.SimulacaoResponse;
import br.com.caixa.mapper.SimulacaoMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulacoes", description = "Endpoints para criar simulacoes de investimento e consultar historico. Requerem autenticacao via token JWT.")
@RolesAllowed("user")
@SecurityRequirement(name = "bearerAuth")
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Operation(
            summary = "Criar simulacao de investimento",
            description = "Realiza uma simulacao de investimento com base nos parametros informados. "
                    + "O sistema seleciona automaticamente o produto mais adequado conforme tipo, valor e prazo. "
                    + "O calculo usa juros compostos: valorFinal = valor * (1 + rentabilidadeAnual/12) ^ prazoMeses. "
                    + "O cliente e identificado automaticamente pelo token JWT.")
    @RequestBody(
            description = "Parametros da simulacao",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SimulacaoRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "CDB basico",
                                    summary = "Simulacao de CDB com R$ 10.000 por 12 meses",
                                    value = """
                                            {
                                              "valor": 10000.00,
                                              "prazoMeses": 12,
                                              "tipoProduto": "CDB"
                                            }"""),
                            @ExampleObject(
                                    name = "LCI com risco baixo",
                                    summary = "Simulacao de LCI com risco especifico",
                                    value = """
                                            {
                                              "valor": 50000.00,
                                              "prazoMeses": 24,
                                              "tipoProduto": "LCI",
                                              "risco": "BAIXO"
                                            }""")
                    }))
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Simulacao realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = SimulacaoResponse.class))),
            @APIResponse(
                    responseCode = "400",
                    description = "Parametros invalidos (campos ausentes, valores negativos, etc.)",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @APIResponse(
                    responseCode = "401",
                    description = "Token JWT ausente ou invalido"),
            @APIResponse(
                    responseCode = "422",
                    description = "Nenhum produto elegivel para os parametros informados",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public Response criar(@Valid SimulacaoRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse(400, "Corpo da requisicao e obrigatorio"))
                    .build();
        }
        Long clienteId = Long.parseLong(jwt.getSubject());

        Simulacao simulacao = simulacaoService.simular(
                clienteId, request.valor(),
                request.prazoMeses(), request.tipoProduto(), request.risco());

        SimulacaoResponse response = SimulacaoMapper.toResponse(simulacao, simulacao.produto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(
            summary = "Consultar historico de simulacoes",
            description = "Retorna todas as simulacoes realizadas pelo cliente autenticado, "
                    + "ordenadas da mais recente para a mais antiga. "
                    + "O cliente e identificado automaticamente pelo token JWT.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de simulacoes do cliente (pode ser vazia)",
                    content = @Content(schema = @Schema(implementation = HistoricoSimulacaoResponse[].class))),
            @APIResponse(
                    responseCode = "401",
                    description = "Token JWT ausente ou invalido")
    })
    public List<HistoricoSimulacaoResponse> historico() {
        Long clienteId = Long.parseLong(jwt.getSubject());
        return simulacaoService.buscarHistorico(clienteId).stream()
                .map(SimulacaoMapper::toHistorico)
                .toList();
    }

    @GET
    @Path("/agregacao")
    @Operation(
            summary = "Consultar metricas agregadas das simulacoes",
            description = "Retorna metricas consolidadas das simulacoes do cliente autenticado: "
                    + "resumo geral, breakdown por tipo de produto, distribuicao de risco e destaques. "
                    + "O cliente e identificado automaticamente pelo token JWT.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metricas agregadas das simulacoes (retorna valores zerados se nao houver simulacoes)",
                    content = @Content(schema = @Schema(implementation = AgregacaoResponse.class))),
            @APIResponse(
                    responseCode = "401",
                    description = "Token JWT ausente ou invalido")
    })
    public AgregacaoResponse agregacao() {
        Long clienteId = Long.parseLong(jwt.getSubject());
        List<Simulacao> simulacoes = simulacaoService.buscarHistoricoComProduto(clienteId);
        return SimulacaoMapper.toAgregacao(simulacoes);
    }
}
