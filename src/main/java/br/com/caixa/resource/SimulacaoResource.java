package br.com.caixa.resource;

import br.com.caixa.application.SimulacaoService;
import br.com.caixa.domain.Simulacao;
import br.com.caixa.dto.HistoricoSimulacaoResponse;
import br.com.caixa.dto.SimulacaoRequest;
import br.com.caixa.dto.SimulacaoResponse;
import br.com.caixa.mapper.SimulacaoMapper;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulacoes")
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @POST
    @Operation(summary = "Criar simulacao de investimento")
    public Response criar(@Valid SimulacaoRequest request) {
        Simulacao simulacao = simulacaoService.simular(
                request.clienteId(), request.valor(),
                request.prazoMeses(), request.tipoProduto(), request.risco());

        SimulacaoResponse response = SimulacaoMapper.toResponse(simulacao, simulacao.produto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Consultar historico de simulacoes por cliente")
    public List<HistoricoSimulacaoResponse> historico(@QueryParam("clienteId") Long clienteId) {
        return simulacaoService.buscarHistorico(clienteId).stream()
                .map(SimulacaoMapper::toHistorico)
                .toList();
    }
}
