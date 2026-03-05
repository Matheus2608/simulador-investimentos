package br.com.caixa.exception;

import br.com.caixa.application.exception.ProdutoNaoElegivelException;
import br.com.caixa.dto.ErroResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ProdutoNaoElegivelExceptionMapper implements ExceptionMapper<ProdutoNaoElegivelException> {

    private static final Logger LOG = Logger.getLogger(ProdutoNaoElegivelExceptionMapper.class);
    private static final int UNPROCESSABLE_ENTITY = 422;

    @Override
    public Response toResponse(ProdutoNaoElegivelException e) {
        LOG.warnf("Produto nao elegivel: %s", e.getMessage());
        var erro = new ErroResponse(UNPROCESSABLE_ENTITY, e.getMessage());
        return Response.status(UNPROCESSABLE_ENTITY).entity(erro).build();
    }
}
