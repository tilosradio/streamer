package hu.tilos.radio.backend;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import hu.tilos.radio.backend.controller.SearchControllerTest;
import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.flywaydb.core.Flyway;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class TestUtil {

    public static SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyMMdd");

    public static SimpleDateFormat YYYYMMDDHHMM = new SimpleDateFormat("yyyMMddHHmm");


    public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(TestUtil.class.getResourceAsStream("/db.properties"));
        return properties;
    }

    public static DataSource initDatasource() {
        try {
            initSchema();
            Properties properties = loadProperties();
            MysqlDataSource ds = new MysqlDataSource();
            Class.forName(properties.getProperty("jdbc.driver")).newInstance();
            ds.setPassword(properties.getProperty("jdbc.password"));
            ds.setURL(properties.getProperty("jdbc.url"));
            ds.setUser(properties.getProperty("jdbc.user"));
            return ds;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Connection initConnection() {
        try {
            initSchema();
            Properties properties = loadProperties();
            Class.forName(properties.getProperty("jdbc.driver")).newInstance();
            Connection conn = DriverManager.getConnection(properties.getProperty("jdbc.url"), properties.getProperty("jdbc.user"), properties.getProperty("jdbc.password"));
            return conn;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static EntityManagerFactory initPersistence() {
        try {
            initSchema();
            Properties properties = loadProperties();
            properties.setProperty("javax.persistence.jdbc.url", properties.getProperty("jdbc.url"));
            properties.setProperty("javax.persistence.jdbc.driver", properties.getProperty("jdbc.driver"));
            properties.setProperty("javax.persistence.jdbc.user", properties.getProperty("jdbc.user"));
            properties.setProperty("javax.persistence.jdbc.password", properties.getProperty("jdbc.password"));

            return Persistence.createEntityManagerFactory("tilos-test", properties);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void initSchema() {
        try {
            Properties properties = loadProperties();

            JdbcDatabaseTester tester = new JdbcDatabaseTester(
                    properties.getProperty("jdbc.driver"),
                    properties.getProperty("jdbc.url"),
                    properties.getProperty("jdbc.user"),
                    properties.getProperty("jdbc.password"));

            Flyway flyway = new Flyway();
            flyway.setDataSource(properties.getProperty("jdbc.url"), properties.getProperty("jdbc.user"), properties.getProperty("jdbc.password"));
            flyway.setInitOnMigrate(true);
            flyway.repair();
            flyway.migrate();
        } catch (Exception ex) {
            throw new RuntimeException("Can't migrate database", ex);
        }
    }

    public static void initTestData() {
        initTestData("baseData.xml");
    }

    public static void initTestData(String file) {
        try {
            Properties properties = loadProperties();

            initSchema();
            JdbcDatabaseTester tester = new JdbcDatabaseTester(
                    properties.getProperty("jdbc.driver"),
                    properties.getProperty("jdbc.url") + "?sessionVariables=FOREIGN_KEY_CHECKS=0",
                    properties.getProperty("jdbc.user"),
                    properties.getProperty("jdbc.password"));

            tester.getConnection().getConnection();
            tester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

            tester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
            tester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler());
            tester.setDataSet(new FlatXmlDataSet(SearchControllerTest.class.getResourceAsStream(file)));
            //new XmlDataSetWriter(new FileWriter("/tmp/test.xml")).write(new FlatXmlDataSet(SearchControllerTest.class.getResourceAsStream("baseData.xml")));
            tester.onSetup();
            tester.getConnection().close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public static void assertTestData(String file, String tableName) {
        try {
            Properties properties = loadProperties();
            JdbcDatabaseTester tester = new JdbcDatabaseTester(
                    properties.getProperty("jdbc.driver"),
                    properties.getProperty("jdbc.url") + "?sessionVariables=FOREIGN_KEY_CHECKS=0",
                    properties.getProperty("jdbc.user"),
                    properties.getProperty("jdbc.password"));

            tester.getConnection().getConnection();

            tester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

            tester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
            tester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler());
            FlatXmlDataSet flatXmlDataSet = new FlatXmlDataSet(SearchControllerTest.class.getResourceAsStream(file));
            Assertion.assertEquals(flatXmlDataSet.getTable("url"), tester.getConnection().createDataSet().getTable("url"));
            tester.getConnection().close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Produces
    @PersistenceContext
    public EntityManager getEntityManager() {
        return TestUtil.initPersistence().createEntityManager();
    }
}
