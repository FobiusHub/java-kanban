package ru.practicum.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task1;
    private Task task2;

    @BeforeEach
    public void createTasks() {
        task1 = new Task("task1", "description1");
        task1.setId(0);
        task2 = new Task("task2", "description2");
        task2.setId(1);
    }

    @Test
    public void taskShouldBeEqualIfEqualId() {
        task2.setId(0);
        assertEquals(task1, task2);
    }

    @Test
    public void endTimeShouldBeEqualsStartTimeIfDurationIsNull() {
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 0));
        assertEquals(task1.getStartTime(), task1.getEndTime());
    }

}