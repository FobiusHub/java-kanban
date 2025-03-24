package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Получение списка всех задач.
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>(tasks.values());
        return taskList;
    }

    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>(epics.values());
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>(subtasks.values());
        return subtaskList;
    }

    //Удаление всех задач.
    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void clearSubtasks() {
        for(Epic epic : epics.values()) {
            epic.getEpicSubtasks().clear();
        }
        subtasks.clear();
    }

    //Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    //Создание. Сам объект должен передаваться в качестве параметра.
    public int addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        return id++;
    }

    public int addEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        return id++;
    }

    public int addSubtask(Subtask subtask) {
        Epic subtaskEpic = subtask.getEpic();
        if(subtaskEpic == null || !epics.containsKey(subtaskEpic.getId()))
            return -1;
        subtask.setId(id);
        subtasks.put(id, subtask);
        subtaskEpic.addSubtask(subtask);
        return id++;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        int key = task.getId();
        if(tasks.containsKey(key))
            tasks.put(key, task);
    }

    public void updateEpic(Epic epic) {
        int key = epic.getId();
        if(epics.containsKey(key)) {
            Epic toUpdate = epics.get(key);
            toUpdate.setName(epic.getName());
            toUpdate.setDescription(epic.getDescription());
        }
    }

    public void updateSubtask(Subtask subtask) {
        int key = subtask.getId();
        if(subtasks.containsKey(key)) {
            subtasks.put(key, subtask);
            subtask.getEpic().updateSubtask(subtask);
        }
    }

    //Удаление по идентификатору.
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if(epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> subtaskList = epic.getEpicSubtasks();
            for (Subtask subtask : subtaskList) {
                subtasks.remove(subtask.getId());
            }
            epic.clearSubtasks();
            epics.remove(id);
        }
    }

    public void deleteSubtask(int id) {
        if(subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            subtask.getEpic().deleteSubtask(subtask);
        }
    }

    //Получение списка всех подзадач определённого эпика.
    public List<Subtask> getEpicSubtasks (int id) {
        if(!epics.containsKey(id))
            return null;
        return epics.get(id).getEpicSubtasks();
    }

}
