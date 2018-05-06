package pro.taskana.data.generation.util;

import java.util.ArrayList;
import java.util.List;

public class DataWrapper {

    public final List<WorkbasketWrapper> workbaskets;
    public final List<TaskWrapper> tasks;
    
    public DataWrapper(List<WorkbasketWrapper> workbaskets, List<TaskWrapper> tasks) {
        super();
        this.workbaskets = workbaskets;
        this.tasks = tasks;
    }
    
    public DataWrapper union(DataWrapper other) {
        if (other == null || this.equals(other)) {
            return this;
        }
        List<WorkbasketWrapper> allWorkbaskets = new ArrayList<>(workbaskets);
        allWorkbaskets.addAll(other.workbaskets);
        List<TaskWrapper> allTasks = new ArrayList<>(tasks);
        allTasks.addAll(other.tasks);
        return new DataWrapper(allWorkbaskets, allTasks);
    }
    
}
