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

    @BeforeEach
    public void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void taskInHistoryShouldBeOldVersion() {
        Task task = new Task("name", "description");
        task.setId(0);
        historyManager.add(task);

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
        Epic epic = new Epic("epicName", "epicDescription");
        epic.setId(0);
        Subtask subtask = new Subtask("name", "description", epic);
        subtask.setId(1);
        epic.addSubtask(subtask);
        historyManager.add(epic);
        historyManager.add(subtask);

        subtask.setName("newName");
        subtask.setDescription("newDescription");
        subtask.setStatus(Status.DONE);
        subtask.setId(2);
        epic.setId(3);
        epic.setName("newEpicName");
        epic.setDescription("newEpicDescription");
        epic.clearSubtasks();

        Epic oldEpic = (Epic)historyManager.getHistory().get(0);
        Subtask oldSubtask = (Subtask)historyManager.getHistory().get(1);

        Assertions.assertEquals(1, oldSubtask.getId());
        Assertions.assertEquals("name", oldSubtask.getName());
        Assertions.assertEquals("description", oldSubtask.getDescription());
        Assertions.assertEquals(Status.NEW, oldSubtask.getStatus());
        Assertions.assertEquals(Status.NEW, oldEpic.getStatus());
        Assertions.assertEquals(0, oldEpic.getId());
        Assertions.assertEquals("epicName", oldEpic.getName());
        Assertions.assertEquals("epicDescription", oldEpic.getDescription());
        Assertions.assertEquals(1, oldEpic.getEpicSubtasks().size());

    }

    @Test
    public void historySizeDoesNotExceedTenElements(){
        historyManager.add(new Task("name1", "description1"));
        historyManager.add(new Task("name2", "description2"));
        historyManager.add(new Task("name3", "description3"));
        historyManager.add(new Task("name4", "description4"));
        historyManager.add(new Task("name5", "description5"));
        historyManager.add(new Task("name6", "description6"));
        historyManager.add(new Task("name7", "description7"));
        historyManager.add(new Task("name8", "description8"));
        historyManager.add(new Task("name9", "description9"));
        historyManager.add(new Task("name10", "description10"));
        historyManager.add(new Subtask("name11", "description11",
                new Epic("epicName", "epicDescription")));

        Assertions.assertEquals(10, historyManager.getHistory().size());
        Assertions.assertEquals("name2", historyManager.getHistory().get(0).getName());
    }



}