package br.com.caixa.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name = "produtos")
public class Produto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_produto", nullable = false)
    public TipoProduto tipoProduto;

    @Column(name = "rentabilidade_anual", nullable = false, precision = 10, scale = 6)
    public BigDecimal rentabilidadeAnual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Risco risco;

    @Column(name = "prazo_min_meses", nullable = false)
    public Integer prazoMinMeses;

    @Column(name = "prazo_max_meses", nullable = false)
    public Integer prazoMaxMeses;

    @Column(name = "valor_min", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorMin;

    @Column(name = "valor_max", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorMax;

    public boolean isElegivel(BigDecimal valor, int prazoMeses, Risco riscoPretendido) {
        boolean valorDentroDoRange = valor.compareTo(valorMin) >= 0 && valor.compareTo(valorMax) <= 0;

        boolean prazoDentroDoRange = prazoMeses >= prazoMinMeses && prazoMeses <= prazoMaxMeses;

        boolean contemRiscoPretendido = riscoPretendido == null || riscoPretendido.equals(risco);
        
        return valorDentroDoRange && prazoDentroDoRange && contemRiscoPretendido;
    }

    public boolean isElegivel(BigDecimal valor, int prazoMeses) {
        return isElegivel(valor, prazoMeses, null);
    }

    // risco null = todos os niveis
    public static Optional<Produto> findElegivel(TipoProduto tipoProduto, BigDecimal valor, int prazoMeses,
                                                 Risco risco) {
        return find(
                "tipoProduto = :tipo AND valorMin <= :valor AND valorMax >= :valor"
                        + " AND prazoMinMeses <= :prazo AND prazoMaxMeses >= :prazo"
                        + " AND (:risco IS NULL OR risco = :risco)",
                Parameters.with("tipo", tipoProduto)
                        .and("valor", valor)
                        .and("prazo", prazoMeses)
                        .and("risco", risco)
        ).firstResultOptional();
    }
}
