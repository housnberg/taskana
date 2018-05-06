package pro.taskana.data.generation.persistence;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationService;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.data.generation.util.TaskWrapper;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskanaEngineImpl;

public class TaskanaAPI {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaAPI.class);
    
    private TaskanaEngine taskanaEngine;
    private TaskService taskService;
    private ClassificationService classificationService;
    
    
    public TaskanaAPI() throws FileNotFoundException, NoSuchFieldException, SQLException {
        TaskanaEngineConfiguration taskanaConfigutation = new TaskanaEngineConfiguration(DataSourceHandler.getDataSource(), false, false);
        this.taskanaEngine = new TaskanaEngineImpl(taskanaConfigutation);
        ((TaskanaEngineImpl) taskanaEngine).setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
        this.taskService = taskanaEngine.getTaskService();
        this.classificationService = taskanaEngine.getClassificationService();
    }
    
    public void createTasks(List<TaskWrapper> tasks) {
        for (Task task : tasks) {
            try {
                taskService.createTask(task);
            } catch (Exception e) {
                LOGGER.error("Cannot create task beacuse of: {}", e.getMessage());
            }
        } 
    }
    
    public void createClassification(List<ClassificationImpl> classifications) {
        for (ClassificationImpl classification : classifications) {
            try {
                classificationService.createClassification(classification);
            } catch (Exception e) {
                LOGGER.error("Cannot create classification beacuse of: {}", e.getMessage());
            }
        }
    }
}
