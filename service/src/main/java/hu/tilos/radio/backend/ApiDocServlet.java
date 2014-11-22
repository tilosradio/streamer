package hu.tilos.radio.backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(urlPatterns = {"/apidoc", "/apidoc/*"})
public class ApiDocServlet extends HttpServlet {

    private static Logger LOG = LoggerFactory.getLogger(ApiDocServlet.class);

    @Inject
    @Configuration(name = "server.url")
    private String serverUrl;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String relativeUri = request.getRequestURI().substring("/apidoc".length());
        if ("".equals(relativeUri)) {
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            String uri = request.getScheme() + "://" +
                    serverUrl +
                    request.getRequestURI() +
                    (request.getQueryString() != null ? "?" + request.getQueryString() : "");

            resp.setHeader("Location", uri + "/");
            return;
        }
        if ("/".equals(relativeUri)) {
            relativeUri = "/index.html";
        }
        InputStream swaggerResource = getClass().getResourceAsStream("/apidocs" + relativeUri);

        if (swaggerResource == null) {
            swaggerResource = getClass().getResourceAsStream("/apidocs/swagger-ui-2.0.24/" + relativeUri);
        }

        if (swaggerResource == null) {
            LOG.info("apidocs resource is missing " + "/apidocs" + relativeUri);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = swaggerResource.read(buffer)) > 0) {
            resp.getOutputStream().write(buffer, 0, read);
        }
    }

}
