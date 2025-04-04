package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    public void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void taskInHistoryShouldBeOldVersion() {
        Task task = new Task("name", "description");
        historyManager.add(task);
        task.setName("newName");
        task.setDescription("newDescription");

        Assertions.assertEquals("name", historyManager.getHistory().get(0).getName());
        Assertions.assertEquals("description", historyManager.getHistory().get(0).getDescription());
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
        historyManager.add(new Task("name11", "description11"));

        Assertions.assertEquals(10, historyManager.getHistory().size());
        Assertions.assertEquals("name2", historyManager.getHistory().get(0).getName());
    }



}