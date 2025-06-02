package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.exceptions.AddTaskException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected Epic epic2;

    @BeforeEach
    public void createTaskManager() throws IOException {
        initializeManager();
        task = new Task("taskName", "taskDescription");
        task.setStartTime(LocalDateTime.of(2000, 1, 1, 12, 20));
        task.setDuration(120);
        taskManager.addTask(task);
        epic = new Epic("epicName", "epicDescription");
        taskManager.addEpic(epic);
        subtask = new Subtask("subtaskName", "SubtaskDescription", epic);
        subtask.setStartTime(LocalDateTime.of(2000, 1, 13, 12, 25));
        taskManager.addSubtask(subtask);
        epic2 = new Epic("epic2Name", "epic2Description");
        taskManager.addEpic(epic2);
    }

    protected abstract void initializeManager() throws IOException;

    @Test
    public void shouldAddTask() {
        assertEquals(task, taskManager.getTask(0).get());
    }

    @Test
    public void shouldAddEpic() {
        assertEquals(epic, taskManager.getEpic(1).get());
    }

    @Test
    public void shouldAddSubtask() {
        assertEquals(subtask, taskManager.getSubtask(2).get());
    }

    @Test
    public void taskShouldNotChangeAfterAdd() {
        assertEquals("taskName", taskManager.getTask(0).get().getName());
        assertEquals("taskDescription", taskManager.getTask(0).get().getDescription());
    }

    @Test
    public void shouldRemoveEpicsSubtaskAfterClearSubtasks() {
        taskManager.clearSubtasks();
        assertEquals(0, epic.getEpicSubtasks().size());
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask));
    }

    @Test
    public void subtasksShouldBeRemovedIfRemoveEpic() {
        taskManager.deleteEpic(epic.getId());
        assertEquals(0, taskManager.getSubtaskList().size());
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask));
    }

    @Test
    public void epicsSubtaskShouldBeRemovedIfItsRemovedInSubtasks() {
        taskManager.deleteSubtask(subtask.getId());
        assertEquals(0, epic.getEpicSubtasks().size());
    }

    @Test
    public void ifUpdateTaskNotCallTaskShouldNotChanges() {
        Task testTask = taskManager.getTask(0).get();
        testTask.setName("testName");
        assertEquals("taskName", taskManager.getTask(0).get().getName());
    }

    @Test
    public void ifUpdateEpicNotCallEpicShouldNotChanges() {
        Epic testEpic = taskManager.getEpic(1).get();
        testEpic.setName("testName");
        assertEquals("epicName", taskManager.getEpic(1).get().getName());
    }

    @Test
    public void ifUpdateSubtaskNotCallSubtaskShouldNotChanges() {
        Subtask testSubtask = taskManager.getSubtask(2).get();
        testSubtask.setName("testName");
        assertEquals("subtaskName", taskManager.getSubtask(2).get().getName());
    }

    @Test
    public void removedTaskWillBeRemoveFromHistory() {
        taskManager.deleteTask(task.getId());
        taskManager.deleteEpic(epic.getId());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void shouldPrioritizeTasksByDate() {
        //задача не должна попасть в список, если startTime == null
        Task task1 = new Task("name1", "description1");
        taskManager.addTask(task1);
        assertFalse(taskManager.getPrioritizedTasks().contains(task1));
        assertEquals(task, taskManager.getPrioritizedTasks().getFirst());
    }

    @Test
    public void isIntersectWorkCorrectly() {
        //Если добавляемая задача пересекается с существующей, должно быть выброшено исключение
        Task task1 = new Task("name1", "description1");
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 13, 0));
        task1.setDuration(120);
        assertThrows(AddTaskException.class, () -> taskManager.addTask(task1));
        //Если пересечения нет, исключения быть не должно
        task1.setStartTime(LocalDateTime.of(2001, 1, 1, 13, 0));
        assertDoesNotThrow(() -> taskManager.addTask(task1));
        //Исключения быть не должно, если startTime == null
        Task task2 = new Task("name2", "description2");
        assertDoesNotThrow(() -> taskManager.addTask(task2));
    }
}
