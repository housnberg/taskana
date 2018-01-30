package pro.taskana.data.generation.persistence;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to initialize the Database.
 * 
 * @author EL
 */
public class DbInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbInitializer.class);

    private static final String DROP_TABLE_ERROR_CODE_STATE = "SQLCODE=-204, SQLSTATE=42704";
    
    private static final String SQL_PATH = "/sql";
    private static final String DB_DROP_TABLES_SCRIPT = SQL_PATH + "/drop-tables.sql";
    private static final String DB_SCHEMA = SQL_PATH + "/taskana-schema.sql";

    private final StringWriter outWriter = new StringWriter();
    private final PrintWriter logWriter = new PrintWriter(outWriter);
    private final StringWriter errorWriter = new StringWriter();
    private final PrintWriter errorLogWriter = new PrintWriter(errorWriter);

    private final DataSource dataSource;
    private Connection connection;
    private ScriptRunner runner;

    public DbInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
        init();
    }

    /**
     * Initializes the database by creating the database schema.
     *
     * @throws SQLException
     */
    public void initDatabase() {
        clearDatabase();
        runScript(DB_SCHEMA);
        runner.closeConnection();
    }
    
    
    private void init() {
        try {
            connection = dataSource.getConnection();
            runner = new ScriptRunner(connection);
            runner.setStopOnError(false);
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errorLogWriter);
        } catch (SQLException ex) {
            throw new IllegalStateException("Error during Database initialization: " + ex.getMessage());
        }
    }

    private void clearDatabase() {
        runScript(DB_DROP_TABLES_SCRIPT);
    }
    
    private void runScript(String script) {
        try {
            LOGGER.debug(connection.getMetaData().toString());
            
            InputStream is = this.getClass().getResourceAsStream(script);
            InputStreamReader isr = new InputStreamReader(is);
            runner.runScript(isr);
        } catch (Exception ex) {
            LOGGER.error("caught Exception " + ex);
        }
        
        LOGGER.debug(outWriter.toString());
        String errorMsg = errorWriter.toString().trim();

        if (!errorMsg.isEmpty() && !errorMsg.contains(DROP_TABLE_ERROR_CODE_STATE)) {
            LOGGER.error(errorWriter.toString());
        }
    }

}
