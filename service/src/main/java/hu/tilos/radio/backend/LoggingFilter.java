package hu.tilos.radio.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class LoggingFilter implements ContainerRequestFilter {

    private static Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    public LoggingFilter() {

    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.debug(requestContext.getMethod() + " " + requestContext.getUriInfo().getPath());
    }

}
