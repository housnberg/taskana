package pro.taskana.data.generation.util;

import pro.taskana.TaskState;
import pro.taskana.impl.TaskImpl;

public class TaskWrapper extends TaskImpl{

    private static int taskCountInWb = 0;
    
    public TaskWrapper(String workbasketId, TaskState state) {
        String formattedCount = Formatter.format(taskCountInWb++, 5);
        setId(formattedCount + workbasketId);
        setState(state);        
    }
    
    public static void resetTaskCountInWorkbasket() {
        taskCountInWb = 0;
    }
}
