package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;
import org.slf4j.Logger;



public class Smoketest {

    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(Smoketest.class);

    public String ping() {
        LOG.info("Ping has been called");
        return "pong";
    }

    /**
     * @exclude
     */
    @Security(role = Role.ADMIN)
    public String authTest() {
        return "OK";
    }

}
