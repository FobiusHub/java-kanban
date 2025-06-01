package ru.practicum.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void createTasks() {
        epic = new Epic("epic", "description");
        subtask1 = new Subtask("task1", "description1", epic);
        subtask1.setId(1);
        subtask2 = new Subtask("task2", "description2", epic);
        subtask2.setId(1);
    }

    @Test
    public void subtaskShouldBeEqualIfEqualId() {
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void subtaskShouldHaveEpic() {
        assertNotNull(subtask1.getEpic());
    }

}