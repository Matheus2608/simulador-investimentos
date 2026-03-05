package br.com.caixa.resource;

import br.com.caixa.application.TokenService;
import br.com.caixa.domain.Cliente;
import br.com.caixa.dto.ErroResponse;
import br.com.caixa.dto.LoginRequest;
import br.com.caixa.dto.LoginResponse;
import br.com.caixa.dto.RegistroRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticacao", description = "Endpoints de registro e login de clientes. Retornam um token JWT para uso nos endpoints protegidos.")
@PermitAll
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    TokenService tokenService;

    @POST
    @Path("/registrar")
    @Transactional
    @Operation(
            summary = "Registrar novo cliente",
            description = "Cria uma nova conta de cliente com nome, email e senha. "
                    + "Retorna um token JWT que pode ser usado imediatamente para acessar os endpoints protegidos. "
                    + "O email deve ser unico no sistema.")
    @RequestBody(
            description = "Dados de registro do novo cliente",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RegistroRequest.class),
                    examples = @ExampleObject(
                            name = "Registro valido",
                            value = """
                                    {
                                      "nome": "Maria Silva",
                                      "email": "maria@email.com",
                                      "senha": "senha123"
                                    }""")))
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Cliente registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @APIResponse(
                    responseCode = "400",
                    description = "Dados de registro invalidos (campos ausentes ou formato incorreto)",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @APIResponse(
                    responseCode = "409",
                    description = "Email ja cadastrado no sistema",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public Response registrar(@Valid RegistroRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse(400, "Corpo da requisicao e obrigatorio"))
                    .build();
        }
        if (Cliente.findByEmail(request.email()).isPresent()) {
            LOG.warnf("Tentativa de registro com email duplicado: %s", request.email());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErroResponse(409, "Email ja cadastrado"))
                    .build();
        }

        Cliente cliente = Cliente.registrar(request.nome(), request.email(), request.senha());
        LOG.infof("Cliente registrado: id=%d, email=%s", cliente.id, cliente.email);

        String token = tokenService.gerarToken(cliente.id, cliente.email);
        return Response.status(Response.Status.CREATED)
                .entity(new LoginResponse(token))
                .build();
    }

    @POST
    @Path("/login")
    @Operation(
            summary = "Autenticar cliente",
            description = "Valida as credenciais do cliente (email e senha) e retorna um token JWT. "
                    + "O token tem validade de 1 hora e deve ser enviado no header Authorization como Bearer token.")
    @RequestBody(
            description = "Credenciais do cliente",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "Login valido",
                            value = """
                                    {
                                      "email": "maria@email.com",
                                      "senha": "senha123"
                                    }""")))
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Autenticacao bem-sucedida",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @APIResponse(
                    responseCode = "400",
                    description = "Dados de login invalidos (campos ausentes ou formato incorreto)",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @APIResponse(
                    responseCode = "401",
                    description = "Credenciais invalidas (email nao encontrado ou senha incorreta)",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public Response login(@Valid LoginRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse(400, "Corpo da requisicao e obrigatorio"))
                    .build();
        }
        return Cliente.autenticar(request.email(), request.senha())
                .map(cliente -> {
                    LOG.infof("Login bem-sucedido: clienteId=%d", cliente.id);
                    String token = tokenService.gerarToken(cliente.id, cliente.email);
                    return Response.ok(new LoginResponse(token)).build();
                })
                .orElseGet(() -> {
                    LOG.warnf("Falha de login: email=%s", request.email());
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(new ErroResponse(401, "Credenciais invalidas"))
                            .build();
                });
    }
}
