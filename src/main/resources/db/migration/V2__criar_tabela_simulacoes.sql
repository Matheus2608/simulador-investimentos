-- V2: Criacao da tabela de simulacoes
CREATE TABLE simulacoes (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id              INTEGER NOT NULL,
    produto_id              INTEGER NOT NULL,
    produto_nome            TEXT    NOT NULL,
    tipo_produto            TEXT    NOT NULL,
    valor_investido         REAL    NOT NULL,
    prazo_meses             INTEGER NOT NULL,
    rentabilidade_aplicada  REAL    NOT NULL,
    valor_final             REAL    NOT NULL,
    data_simulacao          TEXT    NOT NULL,

    CONSTRAINT chk_sim_valor CHECK (valor_investido > 0),
    CONSTRAINT chk_sim_prazo CHECK (prazo_meses > 0),
    CONSTRAINT fk_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE INDEX idx_simulacoes_cliente_id ON simulacoes(cliente_id);
CREATE INDEX idx_simulacoes_data ON simulacoes(data_simulacao);
