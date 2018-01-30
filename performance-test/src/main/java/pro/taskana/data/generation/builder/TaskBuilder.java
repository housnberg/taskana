package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pro.taskana.data.generation.util.TaskWrapper;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;

public class TaskBuilder {
    
    private List<ClassificationImpl> taskClassifications;
    private Random rnd;
    
    private List<WorkbasketImpl> affectedWorkbaskets;
    private Map<TaskState, Integer> taskDistribution;
    
    public TaskBuilder(List<ClassificationImpl> taskClassifications) {
        this.taskDistribution = new HashMap<>();
        this.affectedWorkbaskets = new ArrayList<>();
        this.taskClassifications = taskClassifications;
        this.rnd = new Random();
    }
    
    
    public TaskBuilder affect(List<WorkbasketImpl> workbaskets) {
        this.affectedWorkbaskets = workbaskets;
        this.taskDistribution = new HashMap<>();
        return this;
    }
    
    public TaskBuilder addTasks(TaskState state, int numberOfTasks) {
        int tasks = numberOfTasks;
        if(taskDistribution.containsKey(state) && taskDistribution.get(state) != null) {
            tasks += taskDistribution.get(state);
        }
        taskDistribution.put(state, tasks);
        return this;
    }
    
    public List<TaskImpl> build() {
        List<TaskImpl> generatedTaks = new ArrayList<>();
        for (WorkbasketImpl wb : affectedWorkbaskets) {
            generatedTaks.addAll(generateTasksForWorkbasket(wb));
        }
        return generatedTaks;
    }
    
    private List<TaskImpl> generateTasksForWorkbasket(WorkbasketImpl workbasket) {
        List<TaskImpl> tasksInWb = new ArrayList<>();
        for (TaskState state : taskDistribution.keySet()) {
            List<TaskImpl> tasksInState = generateTasks(workbasket, state, taskDistribution.get(state));
            tasksInWb.addAll(tasksInState);
        }
        return tasksInWb;
    }
    
    private List<TaskImpl> generateTasks(WorkbasketImpl workbasket, TaskState state, int quantity) {
        List<TaskImpl> tasks = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            TaskImpl task = new TaskWrapper();
            task.setState(state);
            task.setOwner(workbasket.getOwner());
            task.setWorkbasketKey(workbasket.getKey());
            
            int rndIndex = rnd.nextInt(taskClassifications.size());
            ClassificationImpl taskClassification = taskClassifications.get(rndIndex);
            String classificationKey = taskClassification.getKey();
            task.setClassificationKey(classificationKey);
            task.setPrimaryObjRef(createObjectReference("Company A", "System A", "Instance A", "VNR", "1234567"));
            
            tasks.add(task);
        }
        return tasks;
    }
    
     private ObjectReference createObjectReference(String company, String system, String systemInstance, String type,
        String value) {
        ObjectReference objectReference = new ObjectReference();
        objectReference.setCompany(company);
        objectReference.setSystem(system);
        objectReference.setSystemInstance(systemInstance);
        objectReference.setType(type);
        objectReference.setValue(value);
        
        return objectReference;
    }
    

}
