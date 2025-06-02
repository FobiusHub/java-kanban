package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();

        task = new Task("name", "description");
        task.setId(0);
        historyManager.add(task);

        epic = new Epic("epicName", "epicDescription");
        epic.setId(1);
        subtask = new Subtask("name", "description", epic);
        subtask.setId(2);
        epic.addSubtask(subtask);
        historyManager.add(epic);
        historyManager.add(subtask);
    }

    @Test
    public void taskInHistoryShouldBeOldVersion() {
        task.setName("newName");
        task.setDescription("newDescription");
        task.setStatus(Status.DONE);
        task.setId(1);

        Task oldTask = historyManager.getHistory().get(0);

        assertEquals(0, oldTask.getId());
        assertEquals("name", oldTask.getName());
        assertEquals("description", oldTask.getDescription());
        assertEquals(Status.NEW, oldTask.getStatus());
    }

    @Test
    public void taskSubclassInHistoryShouldBeOldVersion() {
        subtask.setName("newName");
        subtask.setDescription("newDescription");
        subtask.setStatus(Status.DONE);
        subtask.setId(2);
        epic.setId(3);
        epic.setName("newEpicName");
        epic.setDescription("newEpicDescription");
        epic.clearSubtasks();

        Epic oldEpic = (Epic) historyManager.getHistory().get(1);
        Subtask oldSubtask = (Subtask) historyManager.getHistory().get(2);

        assertEquals(2, oldSubtask.getId());
        assertEquals("name", oldSubtask.getName());
        assertEquals("description", oldSubtask.getDescription());
        assertEquals(Status.NEW, oldSubtask.getStatus());
        assertEquals(Status.NEW, oldEpic.getStatus());
        assertEquals(1, oldEpic.getId());
        assertEquals("epicName", oldEpic.getName());
        assertEquals("epicDescription", oldEpic.getDescription());
        assertEquals(1, oldEpic.getEpicSubtasks().size());
    }

    @Test
    public void newTaskInHistoryManagerShouldBeLast() {
        assertEquals(subtask, historyManager.getHistory().getLast());
    }

    @Test
    public void shouldBeNoDuplicatesInHistory() {
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    public void removeShouldWorkCorrectly() {
        historyManager.remove(1);
        assertEquals(task, historyManager.getHistory().get(0));
        assertEquals(subtask, historyManager.getHistory().get(1));
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void ifGetAnyTaskNotCallHistoryShouldBeEmpty() {
        HistoryManager otherHistoryManager = new InMemoryHistoryManager();
        Task newTask = new Task("newTaskName", "newTaskDescription");
        assertTrue(otherHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldCorrectlyRemoveFirstTaskInHistory() {
        historyManager.remove(0);
        assertEquals(epic, historyManager.getHistory().getFirst());
        assertEquals(subtask, historyManager.getHistory().getLast());
    }

    @Test
    public void shouldCorrectlyRemoveMiddleTaskInHistory() {
        historyManager.remove(1);
        assertEquals(task, historyManager.getHistory().getFirst());
        assertEquals(subtask, historyManager.getHistory().getLast());
    }

    @Test
    public void shouldCorrectlyRemoveLastTaskInHistory() {
        historyManager.remove(2);
        assertEquals(task, historyManager.getHistory().getFirst());
        assertEquals(epic, historyManager.getHistory().getLast());
    }

}