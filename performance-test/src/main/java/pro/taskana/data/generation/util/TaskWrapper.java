package pro.taskana.data.generation.util;

import pro.taskana.TaskState;
import pro.taskana.impl.TaskImpl;

public class TaskWrapper extends TaskImpl{

    private static int taskCountInWb = 0;
    
    public TaskWrapper(String workbasketId, TaskState state) {
        String formattedCount = String.format("%05d", taskCountInWb++);
        setId(state.toString().substring(0, 2) + formattedCount + workbasketId);
        setState(state);
    }
    
    public static void resetTaskCountInWorkbasket() {
        taskCountInWb = 0;
    }
}
