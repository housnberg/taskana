package pro.taskana.rest.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to configure a DB2 database connection.
 *
 * @author EL, FE
 */
public final class DataSourceHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceHandler.class);

    private static final String JDBC_DRIVER = "jdbcDriver";
    private static final String JDBC_URL = "jdbcUrl";
    private static final String DB_USER_NAME = "dbUserName";
    private static final String DB_PASSWORD = "dbPassword";

    private static final String[] PROPERTIES_FOR_DS = new String[] {JDBC_DRIVER, JDBC_URL, DB_USER_NAME, DB_PASSWORD };

    private static final String PROPERTIES_FILENAME = "rest.properties";
    private static final String HOME_DIRECTORY = "user.home";

    private static DataSource dataSource;

    private DataSourceHandler() {

    }

    /**
     * Returns a {@link DataSource} object with given properties.
     *
     * @return The {@link DataSource} object.
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            String userHomeDirectroy = System.getProperty(HOME_DIRECTORY);
            String propertiesFileName = userHomeDirectroy + "/" + PROPERTIES_FILENAME;
            File file = new File(propertiesFileName);
            if (file.exists() && !file.isDirectory()) {
                dataSource = createDataSourceFromProperties(propertiesFileName);
            }
        }
        return dataSource;
    }


    private static DataSource createDataSourceFromProperties(String propertiesFileName) {
        DataSource currentDatasource = null;
        try (InputStream input = new FileInputStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(input);
            boolean propertiesFileIsComplete = true;
            String warningMessage = "";

            for (String property : PROPERTIES_FOR_DS) {
                String propertyValue = properties.getProperty(property);
                if (propertyValue == null || propertyValue.isEmpty()) {
                    warningMessage += ", " + property + " property missing";
                    propertiesFileIsComplete = false;
                }
            }

            if (propertiesFileIsComplete) {
                currentDatasource = createDatasource(properties.getProperty(JDBC_DRIVER),
                        properties.getProperty(JDBC_URL), properties.getProperty(DB_USER_NAME),
                        properties.getProperty(DB_PASSWORD));
                ((PooledDataSource) currentDatasource).forceCloseAll();
            } else {
                LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException("createDataSourceFromProperties caught Exception " + e);
        }
        return currentDatasource;
    }

    /**
     * This method creates a PooledDataSource, if the needed properties are
     * provided.
     *
     * @param driver
     *            the name of the jdbc driver
     * @param jdbcUrl
     *            the url to which the jdbc driver connects
     * @param username
     *            the user name for database access
     * @param password
     *            the password for database access
     * @return DataSource
     */
    private static DataSource createDatasource(String driver, String jdbcUrl, String username, String password) {
        return new PooledDataSource(driver, jdbcUrl, username, password);
    }
}
