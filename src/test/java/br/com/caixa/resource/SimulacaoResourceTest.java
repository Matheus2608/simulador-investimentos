package br.com.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class SimulacaoResourceTest {

    @Test
    void deveRetornar201AoCriarSimulacao() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "clienteId": 123,
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
                .body("""
                        {
                            "clienteId": 123,
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
                .body("""
                        {
                            "clienteId": -1,
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
                .body("{}")
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarHistoricoPorCliente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "clienteId": 999,
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
                .queryParam("clienteId", 999)
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
}
