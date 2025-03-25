package ru.practicum;

import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Турка", "Помыть турку");
        taskManager.addTask(task1);
        Task task2 = new Task("Кофе", "Сварить кофе");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Ремонт", "Сделать ремонт в комнате");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Старые обои", "Удалить старые обои", epic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Новые обои", "Поклеить новые обои", epic1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Чистая машина", "Помыть машину");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Мойка", "Отвезти машину на мойку", epic2);
        taskManager.addSubtask(subtask3);

        showTaskList(taskManager.getTaskList());
        showEpicList(taskManager.getEpicList());
        showSubtaskList(taskManager.getSubtaskList());
        System.out.println("_".repeat(100));
        epic1.setStatus(Status.IN_PROGRESS);

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        showTaskList(taskManager.getTaskList());
        showEpicList(taskManager.getEpicList());
        showSubtaskList(taskManager.getSubtaskList());
        System.out.println("_".repeat(100));

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        showEpicList(taskManager.getEpicList());
        showSubtaskList(taskManager.getSubtaskList());
        System.out.println("_".repeat(100));

        taskManager.deleteSubtask(subtask3.getId());

        showEpicList(taskManager.getEpicList());
        showSubtaskList(taskManager.getSubtaskList());
        System.out.println("_".repeat(100));

        taskManager.deleteEpic(epic1.getId());

        showEpicList(taskManager.getEpicList());
        showSubtaskList(taskManager.getSubtaskList());
        System.out.println("_".repeat(100));
    }

    public static void showTaskList(List<Task> taskList) {
        for(Task task : taskList) {
            System.out.println(task);
        }
        System.out.println();
    }

    public static void showSubtaskList(List<Subtask> taskList) {
        for(Subtask task : taskList) {
            System.out.println(task);
        }
        System.out.println();
    }

    public static void showEpicList(List<Epic> taskList) {
        for(Epic task : taskList) {
            System.out.println(task);
        }
        System.out.println();
    }
}
