package br.com.caixa.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "simulacoes")
public class Simulacao extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "cliente_id", nullable = false)
    public Long clienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    public Produto produto;

    @Column(name = "produto_nome", nullable = false)
    public String produtoNome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_produto", nullable = false)
    public TipoProduto tipoProduto;

    @Column(name = "valor_investido", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorInvestido;

    @Column(name = "prazo_meses", nullable = false)
    public Integer prazoMeses;

    @Column(name = "rentabilidade_aplicada", nullable = false, precision = 10, scale = 6)
    public BigDecimal rentabilidadeAplicada;

    @Column(name = "valor_final", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorFinal;

    @Column(name = "data_simulacao", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    public LocalDateTime dataSimulacao;

    public static Simulacao criar(Produto produto, Long clienteId, BigDecimal valorInvestido,
                                  Integer prazoMeses, BigDecimal valorFinal) {
        Simulacao simulacao = new Simulacao();
        simulacao.produto = produto;
        simulacao.clienteId = clienteId;
        simulacao.produtoNome = produto.nome;
        simulacao.tipoProduto = produto.tipoProduto;
        simulacao.valorInvestido = valorInvestido;
        simulacao.prazoMeses = prazoMeses;
        simulacao.rentabilidadeAplicada = produto.rentabilidadeAnual;
        simulacao.valorFinal = valorFinal;
        simulacao.dataSimulacao = LocalDateTime.now();
        return simulacao;
    }

    public static List<Simulacao> findByClienteId(Long clienteId) {
        return list("clienteId", Sort.by("dataSimulacao").descending().and("id").descending(), clienteId);
    }

    public static List<Simulacao> findByClienteIdComProduto(Long clienteId) {
        return find("SELECT s FROM Simulacao s LEFT JOIN FETCH s.produto WHERE s.clienteId = ?1 ORDER BY s.dataSimulacao DESC",
                clienteId).list();
    }
}
