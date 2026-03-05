package br.com.caixa.application;

import br.com.caixa.application.exception.ProdutoNaoElegivelException;
import br.com.caixa.domain.Produto;
import br.com.caixa.domain.Risco;
import br.com.caixa.domain.Simulacao;
import br.com.caixa.domain.TipoProduto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SimulacaoService {

    private static final Logger LOG = Logger.getLogger(SimulacaoService.class);

    @Inject
    JurosCompostos jurosCompostos;

    @Transactional
    public Simulacao simular(Long clienteId, BigDecimal valor, int prazoMeses, TipoProduto tipoProduto, Risco risco) {
        LOG.debugf("Simulando: clienteId=%d, tipo=%s, valor=%s, prazo=%d, risco=%s",
                clienteId, tipoProduto, valor, prazoMeses, risco);

        Produto produto = buscarProdutoElegivel(tipoProduto, valor, prazoMeses, risco);
        EstrategiaCalculo estrategia = resolverEstrategia(produto);
        BigDecimal valorFinal = estrategia.calcular(valor, prazoMeses, produto);

        Simulacao simulacao = Simulacao.criar(produto, clienteId, valor, prazoMeses, valorFinal);
        simulacao.persist();

        LOG.infof("Simulacao criada: id=%d, produto=%s, valorFinal=%s", simulacao.id, produto.nome, valorFinal);
        return simulacao;
    }

    public Produto buscarProdutoElegivel(TipoProduto tipoProduto, BigDecimal valor, int prazoMeses, Risco risco) {
        return Produto.findElegivel(tipoProduto, valor, prazoMeses, risco)
                .orElseThrow(() -> new ProdutoNaoElegivelException(
                        "Nenhum produto elegivel para tipo=" + tipoProduto
                                + ", valor=" + valor + ", prazo=" + prazoMeses));
    }

    // por enquanto retorna sempre juros compostos.
    // pode ser alterado para selecionar a estrategia com base no produto/tipo
    private EstrategiaCalculo resolverEstrategia(Produto produto) {
        return jurosCompostos;
    }

    public List<Simulacao> buscarHistorico(Long clienteId) {
        return Simulacao.findByClienteId(clienteId);
    }

    public List<Simulacao> buscarHistoricoComProduto(Long clienteId) {
        return Simulacao.findByClienteIdComProduto(clienteId);
    }
}
