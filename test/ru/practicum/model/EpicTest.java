package ru.practicum.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createTasks() {
        epic = new Epic("epic", "description");
        subtask = new Subtask("subtask", "description", epic);
        subtask.setId(0);
        epic.addSubtask(subtask);
    }


    @Test
    public void epicShouldBeEqualIfEqualId() {
        Epic epic1 = new Epic("task1", "description1");
        epic1.setId(1);
        Epic epic2 = new Epic("task2", "description2");
        epic2.setId(1);

        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    public void epicShouldBeNew() {
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldBeInProgress() {
        subtask.setStatus(Status.IN_PROGRESS);
        epic.updateSubtask(subtask);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeDone() {
        subtask.setStatus(Status.DONE);
        epic.updateSubtask(subtask);
        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldKnowSubtasks() {
        Assertions.assertEquals(subtask, epic.getEpicSubtasks().get(0));
    }

    @Test
    public void epicShouldCorrectlyRemoveSubtask() {
        epic.deleteSubtask(subtask);
        int epicSubtasksSize = epic.getEpicSubtasks().size();
        Assertions.assertEquals(0, epicSubtasksSize);
    }

    @Test
    public void doneEpicShouldBeNewAfterRemoveOnlyOneSubtask() {
        subtask.setStatus(Status.DONE);
        epic.updateSubtask(subtask);
        epic.deleteSubtask(subtask);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }


}