package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
        task = new Task("name", "description");
        taskManager.addTask(task);
        epic = new Epic("name", "description");
        taskManager.addEpic(epic);
        subtask = new Subtask("name", "description", epic);
        taskManager.addSubtask(subtask);

    }

    @Test
    public void shouldAddTask() {
        Assertions.assertEquals(task, taskManager.getTask(0));
    }

    @Test
    public void shouldAddEpic() {
        Assertions.assertEquals(epic, taskManager.getEpic(1));
    }

    @Test
    public void shouldAddSubtask() {
        Assertions.assertEquals(subtask, taskManager.getSubtask(2));
    }

    @Test
    public void taskShouldNotChangeAfterAdd() {
        Assertions.assertEquals("name", taskManager.getTask(0).getName());
        Assertions.assertEquals("description", taskManager.getTask(0).getDescription());
    }

    @Test
    public void shouldRemoveEpicsSubtaskAfterClearSubtasks(){
        taskManager.clearSubtasks();
        Assertions.assertEquals(0, epic.getEpicSubtasks().size());
    }

    @Test
    public void subtasksShouldBeRemovedIfRemoveEpic(){
        taskManager.deleteEpic(epic.getId());
        Assertions.assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    public void epicsSubtaskShouldBeRemovedIfItsRemovedInSubtasks(){
        taskManager.deleteSubtask(subtask.getId());
        Assertions.assertEquals(0, epic.getEpicSubtasks().size());
    }

}