# Simulador de Investimentos

API REST para simulacao de investimentos, desenvolvida com Quarkus e Java 21. Seleciona produtos elegiveis a partir de parametros do cliente, calcula o retorno com juros compostos e persiste o historico de simulacoes.

## Requisitos

- Java 21+
- Maven 3.9+ (ou use o wrapper `./mvnw` incluso)
- Docker (opcional)

## Como executar

```bash
# modo desenvolvimento (hot reload + Swagger UI)
./mvnw quarkus:dev
```

A aplicacao sobe em http://localhost:8080. O Swagger UI fica disponivel em http://localhost:8080/q/swagger-ui.

O banco SQLite (`simulador-investimentos.db`) e criado automaticamente no diretorio do projeto. As migrations do Flyway rodam no startup e fazem o seed de 6 produtos.

### Docker

```bash
docker build -t simulador-investimentos .
docker run -p 8080:8080 simulador-investimentos
```

## Testes

```bash
./mvnw test
```

Os testes usam um banco SQLite separado (`target/test-simulador.db`) que e recriado a cada execucao.

## Endpoints

### POST /simulacoes

Cria uma simulacao de investimento.

```json
{
  "clienteId": 123,
  "valor": 10000,
  "prazoMeses": 12,
  "tipoProduto": "CDB",
  "risco": "BAIXO"
}
```

- `tipoProduto`: `CDB`, `LCI` ou `LCA`
- `risco`: `BAIXO`, `MEDIO` ou `ALTO` (opcional — se nao informado, considera todos)

Retorna `201` com o resultado da simulacao ou `422` se nenhum produto for elegivel.

### GET /simulacoes?clienteId=123

Retorna o historico de simulacoes do cliente.

## Produtos disponiveis (seed)

| Produto              | Tipo | Rentabilidade | Risco | Prazo       | Valor              |
|----------------------|------|---------------|-------|-------------|---------------------|
| CDB CAIXA 2026       | CDB  | 12% a.a.     | BAIXO | 3-36 meses  | R$ 1.000 - 500.000  |
| CDB CAIXA Premium    | CDB  | 13.5% a.a.   | MEDIO | 6-60 meses  | R$ 5.000 - 1.000.000|
| LCI CAIXA Habitacao  | LCI  | 9.5% a.a.    | BAIXO | 6-24 meses  | R$ 5.000 - 500.000  |
| LCI CAIXA Plus       | LCI  | 10.5% a.a.   | MEDIO | 12-36 meses | R$ 10.000 - 1.000.000|
| LCA CAIXA Agro       | LCA  | 10% a.a.     | BAIXO | 6-24 meses  | R$ 2.000 - 300.000  |
| LCA CAIXA Agro Premium| LCA | 11.5% a.a.   | MEDIO | 12-48 meses | R$ 10.000 - 500.000 |
