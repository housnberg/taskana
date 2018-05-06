package pro.taskana.data.generation.persistence;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.data.generation.util.ClassificationWrapper;
import pro.taskana.data.generation.util.TaskWrapper;
import pro.taskana.data.generation.util.WorkbasketWrapper;
import pro.taskana.ClassificationService;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketService;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.impl.WorkbasketImpl;

public class TaskanaAPI {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaAPI.class);
    
    private TaskanaEngine taskanaEngine;
    private TaskService taskService;
    private ClassificationService classificationService;
    private WorkbasketService workbasketService;
    
    public TaskanaAPI() throws FileNotFoundException, NoSuchFieldException, SQLException {
        TaskanaEngineConfiguration taskanaConfigutation = new TaskanaEngineConfiguration(DataSourceHandler.getDataSource(), false, false);
        this.taskanaEngine = new TaskanaEngineImpl(taskanaConfigutation);
        taskanaEngine.setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
        this.taskService = taskanaEngine.getTaskService();
        this.classificationService = taskanaEngine.getClassificationService();
        this.workbasketService = taskanaEngine.getWorkbasketService();
    }

    /**
     * Persists all given Tasks via the Taskana API.
     * @param tasks The Tasks to persist.
     */
    public void createTasks(List<TaskWrapper> tasks) {
        for (Task task : tasks) {

            try {
                taskService.createTask(task);
            } catch (Exception e) {
                LOGGER.error("Cannot create task because of: {}", e.getMessage());
            }
        } 
    }

    /**
     * Persists all given Classifications via the Taskana API.
     * @param classifications The {@link pro.taskana.Classification}s to persist.
     */
    public void createClassification(List<ClassificationImpl> classifications) {
        for (ClassificationImpl classification : classifications) {
            try {
                classificationService.createClassification(classification);
            } catch (Exception e) {
                LOGGER.error("Cannot create classification because of: {}", e.getMessage());
            }
        }
    }

    /**
     * Persists all given Workbaskets via the Taskana API.
     * @param workbaskets The {@link Workbasket}s to persist.
     */
    public void createWorkbaskets(List<WorkbasketImpl> workbaskets) {
        for (WorkbasketImpl workbasket : workbaskets) {
            try {
                workbasketService.createWorkbasket(workbasket);
                for (WorkbasketWrapper distributionTarget : ((WorkbasketWrapper) workbasket).getDirectChildren()) {
                    workbasketService.addDistributionTarget(workbasket.getId(), distributionTarget.getId());
                }
            } catch (Exception e) {
                LOGGER.error("Cannot create workbasket because of: {}", e.getMessage());
            }
        }
    }

    /**
     * Persists all given Workbaskets via the Taskana API.
     * @param workbasketAccessItems The {@link WorkbasketAccessItem}s to persist.
     */
    public void createWorkbasketAccesItem(List<WorkbasketAccessItemImpl> workbasketAccessItems) {
        for (WorkbasketAccessItemImpl workbasketAccessItem : workbasketAccessItems) {
            try {
                workbasketService.createWorkbasketAccessItem(workbasketAccessItem);
            } catch (Exception e) {
                LOGGER.error("Cannot create workbasketAccessItem because of: {}", e.getMessage());
            }
        }
    }
}
