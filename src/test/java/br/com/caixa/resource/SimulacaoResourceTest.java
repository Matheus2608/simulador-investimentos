package br.com.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class SimulacaoResourceTest {

    private String gerarToken(Long clienteId) {
        return Jwt.issuer("simulador-investimentos")
                .subject(clienteId.toString())
                .upn("test@test.com")
                .groups(Set.of("user"))
                .expiresIn(Duration.ofMinutes(10))
                .sign();
    }

    @Test
    void deveRetornar201AoCriarSimulacao() {
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + gerarToken(123L))
                .body("""
                        {
                            "valor": 10000,
                            "prazoMeses": 12,
                            "tipoProduto": "CDB"
                        }
                        """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(201)
                .body("produtoValidado.nome", notNullValue())
                .body("produtoValidado.tipo", equalTo("CDB"))
                .body("produtoValidado.risco", notNullValue())
                .body("resultadoSimulacao.valorFinal", greaterThan(10000f))
                .body("resultadoSimulacao.prazoMeses", equalTo(12))
                .body("dataSimulacao", notNullValue());
    }

    @Test
    void deveRetornar422QuandoProdutoNaoElegivel() {
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + gerarToken(123L))
                .body("""
                        {
                            "valor": 1,
                            "prazoMeses": 1,
                            "tipoProduto": "CDB"
                        }
                        """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(422)
                .body("mensagem", containsString("elegivel"));
    }

    @Test
    void deveRetornar400ComCamposInvalidos() {
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + gerarToken(123L))
                .body("""
                        {
                            "valor": -100,
                            "prazoMeses": 0,
                            "tipoProduto": "CDB"
                        }
                        """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400)
                .body("detalhes", not(empty()));
    }

    @Test
    void deveRetornar400ComCamposNulos() {
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + gerarToken(123L))
                .body("{}")
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarHistoricoDoClienteAutenticado() {
        Long clienteId = 999L;
        String token = gerarToken(clienteId);

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body("""
                        {
                            "valor": 10000,
                            "prazoMeses": 12,
                            "tipoProduto": "CDB"
                        }
                        """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].clienteId", equalTo(999))
                .body("[0].produto", notNullValue())
                .body("[0].valorInvestido", equalTo(10000f))
                .body("[0].valorFinal", greaterThan(10000f));
    }

    @Test
    void deveRetornar401SemToken() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "valor": 10000,
                            "prazoMeses": 12,
                            "tipoProduto": "CDB"
                        }
                        """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(401);
    }
}
