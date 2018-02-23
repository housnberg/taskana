package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pro.taskana.data.generation.util.ClassificationType;
import pro.taskana.data.generation.util.TaskWrapper;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskState;
import pro.taskana.impl.WorkbasketImpl;

public class TaskBuilder {
    
    private List<ClassificationImpl> taskClassifications;
    private Random rnd;
    private List<WorkbasketImpl> affectedWorkbaskets;
    private Map<TaskState, Integer> taskDistribution;
    
    private AttachmentBuilder attachmentBuilder;
    private ObjectReferenceBuilder objectReferenceBuilder;
    
    //currently unused because there is no possiblity to map object references to task yet.
    private int minNumOfObjectReferences;
    private int maxNumOfObjectReferences;
    private int numberOfAttachments;
    
    public TaskBuilder(Map<ClassificationType, List<ClassificationImpl>> classifications) {
        this.taskDistribution = new HashMap<>();
        this.affectedWorkbaskets = new ArrayList<>();
        this.taskClassifications = classifications.get(ClassificationType.AUFGABENTYP);
        this.rnd = new Random();
        attachmentBuilder = new AttachmentBuilder(classifications);
        objectReferenceBuilder = new ObjectReferenceBuilder(200);
    }
    
    
    public TaskBuilder affect(List<WorkbasketImpl> workbaskets) {
        this.affectedWorkbaskets = workbaskets;
        this.taskDistribution = new HashMap<>();
        this.maxNumOfObjectReferences = 1;
        this.minNumOfObjectReferences = 1;
        this.numberOfAttachments = 0;
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
    
    public TaskBuilder withObjectReferences(int exactNumberOfObjectReferences) {
        minNumOfObjectReferences = exactNumberOfObjectReferences;
        maxNumOfObjectReferences = exactNumberOfObjectReferences;
        return this;
    }
    
    public TaskBuilder withObjectReferences(int minNumberOfObjectReferences, int maxNumberOfObjectReferences) {
        this.minNumOfObjectReferences = minNumberOfObjectReferences;
        this.maxNumOfObjectReferences = maxNumberOfObjectReferences;
        return this;
    }
    
    public TaskBuilder withAttachments(int numberOfAttachments) {
        this.numberOfAttachments = numberOfAttachments;
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
            task.setPrimaryObjRef(objectReferenceBuilder.getObjectReference());
            task.setAttachments(attachmentBuilder.getAttachments(numberOfAttachments));
            tasks.add(task);
        }
        return tasks;
    }
    

    

}
