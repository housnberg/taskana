package pro.taskana.data.generation.persistence;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.data.generation.util.WorkbasketWrapper;
import pro.taskana.impl.WorkbasketImpl;

/**
 * This class is used to persist the generated performance test data.
 * 
 * @author EL
 */
public class PersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);
    
    private DataSource dataSource;
    private Connection connection;

    private PreparedStatement insertIntoWorkbasket;
    private PreparedStatement insertIntoDistTarget;
    private PreparedStatement insertIntoWbAccessList;
    
    public PersistenceService() {
        this.init();
    }

    private void init() {
        try {
            dataSource = DataSourceHandler.getDataSource();
            connection = dataSource.getConnection();

            DbInitializer dbInitializer = new DbInitializer(dataSource);
            dbInitializer.initDatabase();
        } catch (FileNotFoundException | NoSuchFieldException | SQLException ex) {
            throw new IllegalStateException("Error during PersistenceService initialization: " + ex.getMessage());
        }
        
    }

    /**
     * Persist all {@link Workbasket} into the database.
     * 
     * @param generatedWorkbaskets the list of {@link Workbasket} to persist.
     */
    public void persistWorkbaskets(List<WorkbasketImpl> generatedWorkbaskets) {
        try {
            insertIntoWorkbasket = connection.prepareStatement(PersistenceServiceHelper.SQL_CREATE_WORKBASKET);
            insertIntoDistTarget = connection.prepareStatement(PersistenceServiceHelper.SQL_CREATE_DISTRIBUTION_TARGET);
            
            for (Workbasket workbasket : generatedWorkbaskets) {
                PersistenceServiceHelper.setWorkbasketParams(insertIntoWorkbasket, workbasket);
                insertIntoWorkbasket.execute();
                
                WorkbasketWrapper wbw = (WorkbasketWrapper) workbasket;
                List<WorkbasketWrapper> distributionTargets = wbw.getDirectChildren();
                for (WorkbasketImpl distriutionTarget : distributionTargets) {
                    PersistenceServiceHelper.setDistributionTargetParams(insertIntoDistTarget, workbasket, distriutionTarget);
                    insertIntoDistTarget.execute();
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error during Workbasket persist: " + ex.getMessage());
        } finally {
            try {
                insertIntoWorkbasket.close();
                insertIntoDistTarget.close();
            } catch (SQLException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    /**
     * Persist all {@link WorkbasketAccessItem} into the database.
     * 
     * @param generatedAccessItems The list of {@link WorkbasketAccessItem} to persist.
     */
    public void persistAccessItems(List<WorkbasketAccessItem> generatedAccessItems) {
        try {
            insertIntoWbAccessList = connection.prepareStatement(PersistenceServiceHelper.SQL_CREATE_WORKBASKET_ACCESS_LIST);

            for (WorkbasketAccessItem workbasketAccessItem : generatedAccessItems) {
                PersistenceServiceHelper.setWorkbasketAccessItemParams(insertIntoWbAccessList, workbasketAccessItem);

                insertIntoWbAccessList.execute();
            }
        } catch (SQLException ex) {
            LOGGER.error("Error during AccessItem persist: " + ex.getMessage());
        } finally {
            try {
                insertIntoWbAccessList.close();
            } catch (SQLException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }
}
