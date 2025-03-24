import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 0;
    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, Subtask> subtaskList;

    TaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subtaskList = new HashMap<>();
    }

    //Получение списка всех задач.
    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    //Удаление всех задач.
    public void clearTaskList() {
        taskList.clear();
    }

    public void clearEpicList() {
        clearSubtaskList();
        epicList.clear();
    }

    public void clearSubtaskList() {
        for(Subtask subtask : subtaskList.values()) {
            subtask.getEpic().clearSubtasks();
        }
        subtaskList.clear();
    }

    //Получение по идентификатору.
    public Task getTask(int id) {
        return taskList.get(id);
    }

    public Epic getEpic(int id) {
        return epicList.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtaskList.get(id);
    }

    //Создание. Сам объект должен передаваться в качестве параметра.
    public void addTask(Task task) {
        task.setId(id);
        taskList.put(id, task);
        id++;
    }

    public void addEpic(Epic epic) {
        epic.setId(id);
        epicList.put(id, epic);
        id++;
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(id);
        subtaskList.put(id, subtask);
        id++;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        subtask.getEpic().updateStatus();
    }

    //Удаление по идентификатору.
    public void deleteTask(int id) {
        taskList.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epicList.get(id);
        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for(Subtask subtask : subtasks) {
            subtaskList.remove(subtask.getId());
        }
        epic.clearSubtasks();
        epicList.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtaskList.get(id);
        subtaskList.remove(id);
        subtask.getEpic().deleteSubtask(subtask);
    }

    //Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> getEpicSubtasks (int id) {
        return epicList.get(id).getSubtasks();
    }

}
