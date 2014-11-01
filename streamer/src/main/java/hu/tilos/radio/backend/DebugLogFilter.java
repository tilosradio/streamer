package hu.tilos.radio.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

public class DebugLogFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(DebugLogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        HttpServletRequest hsr = (HttpServletRequest) servletRequest;
        Enumeration en = hsr.getHeaderNames();
        StringBuilder builder = new StringBuilder();
        builder.append("New request " + hsr.getRequestURI());
        builder.append(" | ");
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            builder.append(key + ":" + hsr.getHeader(key) + " | ");

        }
        LOG.debug(builder.toString());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
