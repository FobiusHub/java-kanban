package ru.practicum.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void taskShouldBeEqualIfEqualId(){
        Task task1 = new Task("task1", "description1");
        task1.setId(1);
        Task task2 = new Task("task2", "description2");
        task2.setId(1);

        Assertions.assertEquals(task1, task2);
    }

}