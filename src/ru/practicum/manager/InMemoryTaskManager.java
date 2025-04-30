package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //Получение списка всех задач.
    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    //Удаление всех задач.
    @Override
    public void clearTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        clearSubtasks();
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
    }

    //Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id).clone();
        if (task != null)
            historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id).clone();
        if (epic != null)
            historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id).clone();
        if (subtask != null)
            historyManager.add(subtask);
        return subtask;
    }

    //Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        return id++;
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        return id++;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        Epic subtaskEpic = subtask.getEpic();
        if (subtaskEpic == null || !epics.containsKey(subtaskEpic.getId()))
            return -1;
        subtask.setId(id);
        subtasks.put(id, subtask);
        subtaskEpic.addSubtask(subtask);
        return id++;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        int key = task.getId();
        if (tasks.containsKey(key))
            tasks.put(key, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        int key = epic.getId();
        if (epics.containsKey(key)) {
            Epic toUpdate = epics.get(key);
            toUpdate.setName(epic.getName());
            toUpdate.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int key = subtask.getId();
        if (subtasks.containsKey(key)) {
            subtasks.put(key, subtask);
            subtask.getEpic().updateSubtask(subtask);
        }
    }

    //Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> subtaskList = epic.getEpicSubtasks();
            for (Subtask subtask : subtaskList) {
                int removedId = subtask.getId();
                subtasks.remove(removedId);
                historyManager.remove(removedId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            subtask.getEpic().deleteSubtask(subtask);
            historyManager.remove(id);
        }
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        if (!epics.containsKey(id))
            return null;
        return epics.get(id).getEpicSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
