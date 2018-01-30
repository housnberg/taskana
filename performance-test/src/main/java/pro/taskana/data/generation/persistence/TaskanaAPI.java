package pro.taskana.data.generation.persistence;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationService;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.model.WorkbasketType;

public class TaskanaAPI {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaAPI.class);
    
    private TaskanaEngine taskanaEngine;
    private TaskService taskService;
    private ClassificationService classificationService;
    private WorkbasketService wbService;
    
    
    public TaskanaAPI() throws FileNotFoundException, NoSuchFieldException, SQLException {
        TaskanaEngineConfiguration taskanaConfigutation = new TaskanaEngineConfiguration(DataSourceHandler.getDataSource(), false, false);
        this.taskanaEngine = new TaskanaEngineImpl(taskanaConfigutation);
        ((TaskanaEngineImpl) taskanaEngine).setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
        this.taskService = taskanaEngine.getTaskService();
        this.classificationService = taskanaEngine.getClassificationService();
        this.wbService = taskanaEngine.getWorkbasketService();
    }
    
    public void createTasks(List<TaskImpl> tasks) {
        for (TaskImpl task : tasks) {
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
       
    public void createClassification() {
        try {
            classificationService.createClassification(classificationService.newClassification("a", "b", "c"));
        } catch (ClassificationAlreadyExistException ex) {
            java.util.logging.Logger.getLogger(TaskanaAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
      public void createWorkbasket() {
        try {
            Workbasket wb = wbService.newWorkbasket();
            wb.setKey("AWB000000#00");
            wb.setType(WorkbasketType.TOPIC);
            wb.setDomain("A");
            wb.setName("ASass");
            wbService.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            java.util.logging.Logger.getLogger(TaskanaAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WorkbasketNotFoundException ex) {
            java.util.logging.Logger.getLogger(TaskanaAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotAuthorizedException ex) {
            java.util.logging.Logger.getLogger(TaskanaAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

}
