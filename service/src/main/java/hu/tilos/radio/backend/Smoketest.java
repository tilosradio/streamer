package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;
import org.slf4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("api/v1")
public class Smoketest {

    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(Smoketest.class);

    @GET
    @Path("test/ping")
    @Security(role = Role.GUEST)
    public String ping() {
        LOG.info("Ping has been called");
        return "pong";
    }

    /**
     * @exclude
     */
    @GET
    @Path("test/auth")
    @Security(role = Role.ADMIN)
    public String authTest() {
        return "OK";
    }

}
