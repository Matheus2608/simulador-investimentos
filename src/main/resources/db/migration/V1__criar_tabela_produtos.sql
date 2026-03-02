-- V1: Criacao da tabela de produtos de investimento
CREATE TABLE produtos (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT    NOT NULL,
    tipo_produto    TEXT    NOT NULL,
    rentabilidade_anual REAL NOT NULL,
    risco           TEXT    NOT NULL,
    prazo_min_meses INTEGER NOT NULL,
    prazo_max_meses INTEGER NOT NULL,
    valor_min       REAL    NOT NULL,
    valor_max       REAL    NOT NULL,

    CONSTRAINT chk_rentabilidade CHECK (rentabilidade_anual > 0),
    CONSTRAINT chk_prazo CHECK (prazo_min_meses > 0 AND prazo_max_meses >= prazo_min_meses),
    CONSTRAINT chk_valor CHECK (valor_min > 0 AND valor_max >= valor_min),
    CONSTRAINT chk_risco CHECK (risco IN ('BAIXO', 'MEDIO', 'ALTO')),
    CONSTRAINT chk_tipo CHECK (tipo_produto IN ('CDB', 'LCI', 'LCA'))
);
