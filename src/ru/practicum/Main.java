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
        Subtask subtask3 = new Subtask("Новые плинтус", "Установить новый плинтус", epic1);
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Чистая машина", "Помыть машину");
        taskManager.addEpic(epic2);

        System.out.println("История пустая, пока нет ни одного обращения к задачам");
        System.out.println(taskManager.getHistory());
        System.out.println("_".repeat(100));

        System.out.println("Каждый запрос добавляется в историю: ");
        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());
        taskManager.getEpic(epic2.getId());
        showTaskList(taskManager.getHistory());

        System.out.println("Запросы всех типов задач не дублируются в истории: ");
        taskManager.getTask(0);
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        showTaskList(taskManager.getHistory());

        System.out.println("Последняя просмотренная задача будет последней: ");
        taskManager.getTask(0);
        showTaskList(taskManager.getHistory());

        System.out.println("Последняя удаления задачи она пропадет и из истории: ");
        taskManager.deleteTask(0);
        showTaskList(taskManager.getHistory());

        System.out.println("Последняя удаления эпика он пропадет из истории вместе со своими подзадачами: ");
        taskManager.deleteEpic(epic1.getId());
        showTaskList(taskManager.getHistory());
    }

    public static <T extends Task> void showTaskList(List<T> taskList) {
        for (T task : taskList) {
            System.out.println(task);
        }
        System.out.println("_".repeat(100));
        System.out.println();
    }

}
