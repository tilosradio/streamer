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

}
