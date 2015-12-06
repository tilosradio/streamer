package hu.radio.tilos;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class PrerenderApplication extends Application<PrerenderConfiguration> {

    public static void main(String[] args) throws Exception {
        new PrerenderApplication().run(args);
    }

    @Override
    public String getName() {
        return "prerender";
    }

    @Override
    public void initialize(Bootstrap<PrerenderConfiguration> bootstrap) {

    }

    @Override
    public void run(PrerenderConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().disable();
        environment.getApplicationContext().addServlet(PrerenderServlet.class, "/*");
    }
}
