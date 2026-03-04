package br.com.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthResourceTest {

    private String emailUnico() {
        return UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }

    @Test
    void deveRegistrarNovoCliente() {
        String email = emailUnico();
        given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Teste Silva",
                            "email": "%s",
                            "senha": "senha123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(201)
                .body("token", notNullValue());
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastrado() {
        String email = emailUnico();
        given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Primeiro",
                            "email": "%s",
                            "senha": "senha123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Segundo",
                            "email": "%s",
                            "senha": "outra123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(409)
                .body("mensagem", containsString("cadastrado"));
    }

    @Test
    void deveRetornar400ComCamposVaziosNoRegistro() {
        given()
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornar400ComSenhaCurta() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Teste",
                            "email": "%s",
                            "senha": "123"
                        }
                        """.formatted(emailUnico()))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(400);
    }

    @Test
    void deveAutenticarComCredenciaisValidas() {
        String email = emailUnico();
        given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Login Teste",
                            "email": "%s",
                            "senha": "senha123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {
                            "email": "%s",
                            "senha": "senha123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void deveRetornar401ComSenhaErrada() {
        String email = emailUnico();
        given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Senha Errada",
                            "email": "%s",
                            "senha": "senha123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {
                            "email": "%s",
                            "senha": "errada123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("mensagem", containsString("invalidas"));
    }

    @Test
    void deveRetornar401ComEmailInexistente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "email": "naoexiste@test.com",
                            "senha": "senha123"
                        }
                        """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("mensagem", containsString("invalidas"));
    }

    @Test
    void deveRetornar400ComCamposVaziosNoLogin() {
        given()
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    void deveUsarTokenDoRegistroParaAcessarEndpointProtegido() {
        String email = emailUnico();
        String token = given()
                .contentType("application/json")
                .body("""
                        {
                            "nome": "Token Teste",
                            "email": "%s",
                            "senha": "senha123"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/registrar")
                .then()
                .statusCode(201)
                .extract().path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200);
    }
}
