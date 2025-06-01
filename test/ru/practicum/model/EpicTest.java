package ru.practicum.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private Subtask subtask;
    private Subtask subtask1;

    @BeforeEach
    public void createTasks() {
        epic = new Epic("epic", "description");
        subtask = new Subtask("subtask", "description", epic);
        subtask.setId(0);
        epic.addSubtask(subtask);
        subtask1 = new Subtask("subtask1", "description1", epic);
        subtask1.setId(1);
        epic.addSubtask(subtask1);
    }

    @Test
    public void epicShouldBeEqualIfEqualId() {
        Epic epic1 = new Epic("task1", "description1");
        epic1.setId(1);
        Epic epic2 = new Epic("task2", "description2");
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }

    @Test
    public void epicShouldBeNewIfAllSubtasksAreNew() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldBeDoneIfAllSubtasksAreDone() {
        subtask.setStatus(Status.DONE);
        epic.updateSubtask(subtask);
        subtask1.setStatus(Status.DONE);
        epic.updateSubtask(subtask1);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldBeInProgressIfAtLeastOneSubtaskIsNotDone() {
        subtask.setStatus(Status.DONE);
        epic.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeInProgressIfAllSubtasksInProgress() {
        subtask.setStatus(Status.IN_PROGRESS);
        epic.updateSubtask(subtask);
        subtask1.setStatus(Status.IN_PROGRESS);
        epic.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldKnowSubtasks() {
        assertEquals(subtask, epic.getEpicSubtasks().get(0));
    }

    @Test
    public void epicShouldCorrectlyRemoveSubtask() {
        epic.deleteSubtask(subtask);
        int epicSubtasksSize = epic.getEpicSubtasks().size();
        assertEquals(1, epicSubtasksSize);
    }

    @Test
    public void doneEpicShouldBeNewAfterRemoveOnlyOneSubtask() {
        subtask.setStatus(Status.DONE);
        epic.updateSubtask(subtask);
        epic.deleteSubtask(subtask);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void epicsStartTimeCannotBeSet() {
        epic.setStartTime(LocalDateTime.now());
        assertNull(epic.getStartTime());
    }

    @Test
    public void epicsDurationCannotBeSet() {
        epic.setDuration(1000000);
        assertEquals(0, epic.getDuration());
    }

    @Test
    public void epicsStartTimeShouldBeEqualEarliestSubtask() {
        subtask.setStartTime(LocalDateTime.of(2000, 2, 12, 14, 20));
        //один subtask оставим с незаполненным значением startTime
        Subtask subtask1 = new Subtask("name1", "description1", epic);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("name2", "description2", epic);
        subtask2.setId(2);
        subtask2.setStartTime(LocalDateTime.of(2000, 2, 12, 14, 30));
        epic.updateSubtask(subtask);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        assertEquals(subtask.getStartTime(), epic.getStartTime());
    }

    @Test
    public void epicsDurationEqualsSumOfSubtasksDuration() {
        subtask.setDuration(60);
        epic.updateSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "description1", epic);
        subtask1.setDuration(120);
        subtask1.setId(1);
        epic.addSubtask(subtask1);
        assertEquals(180, epic.getDuration());

    }

    @Test
    public void epicsStartTimeShouldBeNullIfSubtasksIsEmpty() {
        epic.clearSubtasks();
        assertNull(epic.getStartTime());
    }

    @Test
    public void epicsDurationShouldBeZeroIfSubtasksIsEmpty() {
        epic.clearSubtasks();
        assertEquals(0, epic.getDuration());
    }


}