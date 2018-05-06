package pro.taskana.data.generation.util;

import pro.taskana.Classification;
import pro.taskana.TaskState;
import pro.taskana.impl.TaskImpl;

public class TaskWrapper extends TaskImpl{

    private static int taskCountInWb = 0;
    
    private Classification classification;
    
    public TaskWrapper(WorkbasketWrapper workbasket, TaskState state) {
        String formattedCount = Formatter.format(taskCountInWb++, 5);
        setId(formattedCount + workbasket.getKey());
        setState(state);
        workbasket.addTask(this);
    }
    
    public void setClassification(Classification classification) {
        this.setClassificationKey(classification.getKey());
        this.setClassificationCategory(classification.getCategory());
        this.setClassificationSummary(classification.asSummary());
        this.classification = classification;
    }
    
    public Classification getClassification() {
        return this.classification;
    }
    
    public static void resetTaskCountInWorkbasket() {
        taskCountInWb = 0;
    }
}
