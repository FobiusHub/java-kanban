package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public interface TaskManager {
    //Получение списка всех задач.
    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    //Удаление всех задач.
    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    //Получение по идентификатору.
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    //Создание. Сам объект должен передаваться в качестве параметра.
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //Удаление по идентификатору.
    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    //Получение списка всех подзадач определённого эпика.
    List<Subtask> getEpicSubtasks(int id);

    List<Task> getHistory();
}
