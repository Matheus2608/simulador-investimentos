package br.com.caixa.exception;

import br.com.caixa.dto.ErroResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ConstraintViolationExceptionMapper.class);
    private static final int BAD_REQUEST = 400;

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<String> detalhes = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .sorted()
                .toList();

        LOG.debugf("Validacao falhou: %s", detalhes);
        var erro = new ErroResponse(BAD_REQUEST, "Erro de validacao", detalhes);
        return Response.status(BAD_REQUEST).entity(erro).build();
    }
}
