package hu.tilos.radio.backend;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class ModelUpgrader implements ServletContextListener {

    private static Logger LOG = LoggerFactory.getLogger(ModelUpgrader.class);

    @Resource(mappedName = "java:/jdbc/tilos")
    DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.repair();
        flyway.setInitOnMigrate(true);
        flyway.migrate();
        LOG.info("Model has been upgraded");

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
