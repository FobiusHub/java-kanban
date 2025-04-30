package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

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

        Assertions.assertEquals(0, oldTask.getId());
        Assertions.assertEquals("name", oldTask.getName());
        Assertions.assertEquals("description", oldTask.getDescription());
        Assertions.assertEquals(Status.NEW, oldTask.getStatus());
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

        Assertions.assertEquals(2, oldSubtask.getId());
        Assertions.assertEquals("name", oldSubtask.getName());
        Assertions.assertEquals("description", oldSubtask.getDescription());
        Assertions.assertEquals(Status.NEW, oldSubtask.getStatus());
        Assertions.assertEquals(Status.NEW, oldEpic.getStatus());
        Assertions.assertEquals(1, oldEpic.getId());
        Assertions.assertEquals("epicName", oldEpic.getName());
        Assertions.assertEquals("epicDescription", oldEpic.getDescription());
        Assertions.assertEquals(1, oldEpic.getEpicSubtasks().size());
    }

    @Test
    public void newTaskInHistoryManagerShouldBeLast() {
        Assertions.assertEquals(subtask, historyManager.getHistory().getLast());
    }

    @Test
    public void shouldBeNoDuplicatesInHistory() {
        historyManager.add(task);
        historyManager.add(task);
        Assertions.assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    public void removeShouldWorkCorrectly() {
        historyManager.remove(1);
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
        Assertions.assertEquals(subtask, historyManager.getHistory().get(1));
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

}