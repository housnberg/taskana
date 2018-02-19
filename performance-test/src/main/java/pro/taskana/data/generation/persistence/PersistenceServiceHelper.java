package pro.taskana.data.generation.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketSummary;
import pro.taskana.impl.WorkbasketImpl;

/**
 * This class is used as a wrapper for all SQL Queries.
 * 
 * @author EL
 */
public final class PersistenceServiceHelper {
        
    public static final String SQL_CREATE_WORKBASKET = "INSERT INTO WORKBASKET"
            + " (ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION,"
            + " OWNER, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_CREATE_DISTRIBUTION_TARGET = "INSERT INTO DISTRIBUTION_TARGETS"
            + " (SOURCE_ID, TARGET_ID)"
            + " VALUES (?, ?)";
    public static final String SQL_CREATE_WORKBASKET_ACCESS_LIST = "INSERT INTO WORKBASKET_ACCESS_LIST"
            + " (ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE,"
            + " PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final int WORKBASKET_ID = 1;
    private static final int WORKBASKET_KEY = 2;
    private static final int WORKBASKET_CREATED = 3;
    private static final int WORKBASKET_MODIFIED = 4;
    private static final int WORKBASKET_NAME = 5;
    private static final int WORKBASKET_DOMAIN = 6;
    private static final int WORKBASKET_TYPE = 7;
    private static final int WORKBASKET_DESCRIPTION = 8;
    private static final int WORKBASKET_OWNER = 9;
    private static final int WORKBASKET_CUSTOM_1 = 10;
    private static final int WORKBASKET_CUSTOM_2 = 11;
    private static final int WORKBASKET_CUSTOM_3 = 12;
    private static final int WORKBASKET_CUSTOM_4 = 13;
    private static final int WORKBASKET_ORG_LEVEL_1 = 14;
    private static final int WORKBASKET_ORG_LEVEL_2 = 15;
    private static final int WORKBASKET_ORG_LEVEL_3 = 16;
    private static final int WORKBASKET_ORG_LEVEL_4 = 17;
    
    private static final int DISTRIBUTION_TARGET_SOURCE_ID = 1;
    private static final int DISTRIBUTION_TARGET_TARGET_ID = 2;
    
    private static final int WORKBASKET_ACCESS_LIST_ID = 1;
    private static final int WORKBASKET_ACCESS_LIST_WORKBASKET_KEY = 2;
    private static final int WORKBASKET_ACCESS_LIST_ACCESS_ID = 3;
    private static final int WORKBASKET_ACCESS_LIST_PERM_READ = 4;
    private static final int WORKBASKET_ACCESS_LIST_PERM_OPEN = 5;
    private static final int WORKBASKET_ACCESS_LIST_PERM_APPEND = 6;
    private static final int WORKBASKET_ACCESS_LIST_PERM_TRANSFER = 7;
    private static final int WORKBASKET_ACCESS_LIST_PERM_DISTRIBUTE = 8;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_1 = 9;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_2 = 10;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_3 = 11;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_4 = 12;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_5 = 13;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_6 = 14;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_7 = 15;
    private static final int WORKBASKET_ACCESS_LIST_PERM_CUSTOM_8 = 16;
    
    /**
     * Set all {@link Workbasket} parameters for a given {@link PreparedStatement}.
     * The {@link PreparedStatement} should be of type {@link PersistenceServiceHelper#SQL_CREATE_WORKBASKET}.
     * 
     * @param ps The {@link PreparedStatement} of type {@link PersistenceServiceHelper#SQL_CREATE_WORKBASKET}.
     * @param wb The {@link Workbasket} of which the parameters should be set.
     * 
     * @throws SQLException If some errors occur during the setting of parameters.
     */
    public static void setWorkbasketParams(PreparedStatement ps, Workbasket wb) throws SQLException {
    	WorkbasketImpl wbImpl = (WorkbasketImpl) wb;
    	ps.setString(WORKBASKET_ID, wb.getId());
        ps.setTimestamp(WORKBASKET_CREATED, Timestamp.from(wb.getCreated()));
        ps.setTimestamp(WORKBASKET_MODIFIED, Timestamp.from(wb.getModified()));
        ps.setString(WORKBASKET_NAME, wb.getName());
        ps.setString(WORKBASKET_DESCRIPTION, wb.getDescription());
        ps.setString(WORKBASKET_OWNER, wb.getOwner());
        ps.setString(WORKBASKET_KEY, wb.getKey());
        ps.setString(WORKBASKET_DOMAIN, wb.getDomain());
        ps.setString(WORKBASKET_TYPE, wb.getType().name());
        ps.setString(WORKBASKET_CUSTOM_1, wbImpl.getCustom1());
        ps.setString(WORKBASKET_CUSTOM_2, wbImpl.getCustom2());
        ps.setString(WORKBASKET_CUSTOM_3, wbImpl.getCustom3());
        ps.setString(WORKBASKET_CUSTOM_4, wbImpl.getCustom4());
        ps.setString(WORKBASKET_ORG_LEVEL_1, wbImpl.getOrgLevel1());
        ps.setString(WORKBASKET_ORG_LEVEL_2, wbImpl.getOrgLevel2());
        ps.setString(WORKBASKET_ORG_LEVEL_3, wbImpl.getOrgLevel3());
        ps.setString(WORKBASKET_ORG_LEVEL_4, wbImpl.getOrgLevel4());
    }
    
    /**
     * Set all DistributionTarget parameters for a given {@link PreparedStatement}.
     * The {@link PreparedStatement} should be of type {@link PersistenceServiceHelper#SQL_CREATE_DISTRIBUTION_TARGET}.
     * 
     * @param ps The {@link PreparedStatement} of type {@link PersistenceServiceHelper#SQL_CREATE_DISTRIBUTION_TARGET}.
     * @param source The source {@link Workbasket}.
     * @param target The target {@link Workbasket}.
     * 
     * @throws SQLException If some errors occur during the setting of parameters.
     */
    public static void setDistributionTargetParams(PreparedStatement ps, Workbasket source, WorkbasketImpl target) throws SQLException {
        ps.setString(DISTRIBUTION_TARGET_SOURCE_ID, source.getId());
        ps.setString(DISTRIBUTION_TARGET_TARGET_ID, target.getId());
    }
    
    /**
     * Set all {@link WorkbasketAccessItem} parameters for a given {@link PreparedStatement}.
     * The {@link PreparedStatement} should be of type {@link PersistenceServiceHelper#SQL_CREATE_WORKBASKET_ACCESS_LIST}.
     * 
     * @param ps The {@link PreparedStatement} of type {@link PersistenceServiceHelper#SQL_CREATE_WORKBASKET_ACCESS_LIST}.
     * @param wbAccessItem The {@link WorkbasketAccessItem} of which the parameters should be set.
     * 
     * @throws SQLException If some errors occur during the setting of parameters.
     */
    public static void setWorkbasketAccessItemParams(PreparedStatement ps, WorkbasketAccessItem wbAccessItem) throws SQLException {
        ps.setString(WORKBASKET_ACCESS_LIST_ID, wbAccessItem.getId());
        ps.setString(WORKBASKET_ACCESS_LIST_WORKBASKET_KEY, wbAccessItem.getWorkbasketKey());
        ps.setString(WORKBASKET_ACCESS_LIST_ACCESS_ID, wbAccessItem.getAccessId());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_READ, wbAccessItem.isPermRead());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_OPEN, wbAccessItem.isPermOpen());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_APPEND, wbAccessItem.isPermAppend());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_TRANSFER, wbAccessItem.isPermTransfer());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_DISTRIBUTE, wbAccessItem.isPermDistribute());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_1, wbAccessItem.isPermCustom1());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_2, wbAccessItem.isPermCustom2());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_3, wbAccessItem.isPermCustom3());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_4, wbAccessItem.isPermCustom4());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_5, wbAccessItem.isPermCustom5());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_6, wbAccessItem.isPermCustom6());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_7, wbAccessItem.isPermCustom7());
        ps.setBoolean(WORKBASKET_ACCESS_LIST_PERM_CUSTOM_8, wbAccessItem.isPermCustom8());
    }
}
