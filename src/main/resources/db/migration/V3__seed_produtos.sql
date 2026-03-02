-- V3: Seed de produtos de investimento
INSERT INTO produtos (nome, tipo_produto, rentabilidade_anual, risco, prazo_min_meses, prazo_max_meses, valor_min, valor_max)
VALUES
    ('CDB CAIXA 2026',        'CDB', 0.12, 'BAIXO', 3,  36, 1000.00,  500000.00),
    ('CDB CAIXA Premium',     'CDB', 0.135,'MEDIO', 6,  60, 5000.00,  1000000.00),
    ('LCI CAIXA Habitacao',   'LCI', 0.095,'BAIXO', 6,  24, 5000.00,  500000.00),
    ('LCI CAIXA Plus',        'LCI', 0.105,'MEDIO', 12, 36, 10000.00, 1000000.00),
    ('LCA CAIXA Agro',        'LCA', 0.10, 'BAIXO', 6,  24, 2000.00,  300000.00),
    ('LCA CAIXA Agro Premium','LCA', 0.115,'MEDIO', 12, 48, 10000.00, 500000.00);
