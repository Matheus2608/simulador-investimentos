# Decisoes Tecnicas

## Active Record com Panache

As entidades usam o padrao Active Record do Panache (`PanacheEntityBase`) com campos publicos e metodos estaticos de consulta direto na entidade. Exemplos: `Produto.findElegivel()`, `Simulacao.findByClienteId()`, `Cliente.find("email", email)`. Isso mantem queries e logica de dominio proximas dos dados, sem necessidade de repositorios separados pra entidades simples. O trade-off e acoplamento com Panache, mas como o projeto ja depende do Quarkus, faz sentido aproveitar a simplicidade.

## Autenticacao JWT simplificada

A autenticacao usa SmallRye JWT com par de chaves RSA (PEM). O fluxo e basico: registro com bcrypt, login retorna token, endpoints protegidos com `@RolesAllowed`. Nao tem refresh token, revogacao ou roles granulares — so a role `user`. Pra um simulador de investimentos onde o token serve pra associar simulacoes ao cliente, essa abordagem atende sem adicionar complexidade desnecessaria.

## Separacao de responsabilidades

O projeto tenta manter cada classe com uma unica responsabilidade:

- **Resource** so recebe request e devolve response, sem logica de negocio
- **Service** orquestra o fluxo (buscar produto, calcular, persistir)
- **Mapper** converte entre entidades e DTOs
- **CalculoAgregacao** concentra toda a matematica de agregacao, extraido do mapper pra nao misturar transformacao de dados com calculos
- **EstrategiaCalculo** (interface) + **JurosCompostos** (implementacao) separam a formula de calculo do service. Se precisar de outro tipo de calculo (juros simples, indexado ao IPCA), basta criar outra implementacao sem mexer no que ja existe

## SQLite REAL para valores monetarios

As colunas monetarias (`valor_min`, `valor_max`, `valor_investido`, `valor_final`) e de taxas (`rentabilidade_anual`, `rentabilidade_aplicada`) usam o tipo `REAL` do SQLite, que armazena como IEEE 754 double (64 bits). Para os valores deste projeto (ate R$ 1.000.000,00 com 2 casas decimais), double oferece ~15 digitos significativos — bem acima dos ~9 necessarios, sem perda pratica de precisao. Toda a aritmetica na camada Java usa `BigDecimal` com `MathContext.DECIMAL128`, garantindo precisao total nos calculos. A limitacao fica restrita ao armazenamento, onde a conversao double nao afeta os valores dentro desse range.

## Money como value object

A classe `Money` encapsula `BigDecimal` com `MathContext.DECIMAL128` e oferece operacoes aritmeticas imutaveis. Evita espalhar `BigDecimal.add(x, MathContext.DECIMAL128)` pelo codigo e garante que toda conta financeira usa a mesma precisao. O `arredondar()` aplica escala 2 com `HALF_EVEN` (banker's rounding).