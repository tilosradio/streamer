package hu.tilos.radio.backend;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbHelper {

    @Test
    //@Ignore
    public void testSchemaCreation() throws Exception {
        Properties p = TestUtil.loadProperties();

        Class driverClass = Class.forName(p.getProperty("jdbc.driver"));
        Connection jdbcConnection = DriverManager.getConnection(p.getProperty("jdbc.url"), p.getProperty("jdbc.user"), p.getProperty("jdbc.password"));
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // write DTD file
        FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream("src/test/resources/schema.dtd"));
    }
}
