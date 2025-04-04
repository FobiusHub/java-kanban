package ru.practicum;

import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
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

        printAllTasks(taskManager);

        System.out.println("_".repeat(100));
        epic1.setStatus(Status.IN_PROGRESS);

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        taskManager.deleteEpic(epic1.getId());

        System.out.println("_".repeat(100));

        taskManager.getEpic(5);

        printAllTasks(taskManager);
    }

    public static <T extends Task> void showTaskList(List<T> taskList) {
        for (T task : taskList) {
            System.out.println(task);
        }
        System.out.println();
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtaskList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }


}
