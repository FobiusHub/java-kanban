package ru.practicum.manager;

import ru.practicum.exceptions.AddTaskException;
import ru.practicum.exceptions.UpdateTaskException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
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
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        subtasks.keySet().forEach(historyManager::remove);
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        epics.values().forEach(Epic::clearSubtasks);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
    }

    //Получение по идентификатору.
    @Override
    public Optional<Task> getTask(int id) {
        Task task = tasks.get(id).clone();
        if (task != null) {
            historyManager.add(task);
        }
        return Optional.of(task);
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        Epic epic = epics.get(id).clone();
        if (epic != null) {
            historyManager.add(epic);
        }
        return Optional.of(epic);
    }

    @Override
    public Optional<Subtask> getSubtask(int id) {
        Subtask subtask = subtasks.get(id).clone();
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return Optional.of(subtask);
    }

    //Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int addTask(Task task) {
        if (isIntersectWithAny(task)) {
            throw new AddTaskException("Ошибка добавления задачи: обнаружено пересечение");
        }
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
        if (isIntersectWithAny(subtask)) {
            throw new AddTaskException("Ошибка добавления подзадачи: обнаружено пересечение");
        }
        Epic subtaskEpic = subtask.getEpic();
        if (subtaskEpic == null || !epics.containsKey(subtaskEpic.getId())) {
            return -1;
        }
        subtask.setId(id);
        subtasks.put(id, subtask);
        subtaskEpic.addSubtask(subtask);
        return id++;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        if (isIntersectWithAny(task)) {
            throw new UpdateTaskException("Ошибка обновления задачи: обнаружено пересечение");
        }
        int key = task.getId();
        if (tasks.containsKey(key)) {
            tasks.put(key, task);
        }
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
        if (isIntersectWithAny(subtask)) {
            throw new UpdateTaskException("Ошибка обновления подзадачи: обнаружено пересечение");
        }
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
            epics.get(id).getEpicSubtasks().stream()
                    .map(Subtask::getId)
                    .forEach(removedId -> {
                        subtasks.remove(removedId);
                        historyManager.remove(removedId);
                    });
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
        if (!epics.containsKey(id)) {
            return null;
        }
        return epics.get(id).getEpicSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        List<Task> filteredTasks = tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .toList();

        List<Subtask> filteredSubtasks = subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .toList();

        prioritizedTasks.addAll(filteredTasks);
        prioritizedTasks.addAll(filteredSubtasks);

        return prioritizedTasks.stream().toList();
    }

    @Override
    public boolean isIntersect(Task task1, Task task2) {
        if (task1 == null ||
                task1.getStartTime() == null ||
                task2 == null ||
                task2.getStartTime() == null ||
                task1.equals(task2)) {
            return false;
        }

        LocalDateTime task1StartTime = task1.getStartTime();
        LocalDateTime task1EndTime = task1.getEndTime();
        LocalDateTime task2StartTime = task2.getStartTime();
        LocalDateTime task2EndTime = task2.getEndTime();

        return (task1EndTime.isAfter(task2StartTime) && task1StartTime.isBefore(task2StartTime)) ||
                (task2EndTime.isAfter(task1StartTime) && task2StartTime.isBefore(task1StartTime)) ||
                task1StartTime.equals(task2StartTime);

    }

    @Override
    public boolean isIntersectWithAny(Task task) {
        List<Task> allTasks = getPrioritizedTasks();

        for (Task otherTask : allTasks) {
            if (isIntersect(task, otherTask)) {
                return true;
            }
        }

        return false;
    }

}
