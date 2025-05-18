package ru.practicum.manager;

import ru.practicum.exceptions.ManagerSaveException;
import ru.practicum.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public int addTask(Task task) {
        int result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public int addEpic(Epic epic) {
        int result = super.addEpic(epic);
        save();
        return result;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int result = super.addSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    //Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(),
                StandardCharsets.UTF_8, false))) {
            writer.write("id,type,name,status,description,epic\n");
            List<Task> allTasks = new ArrayList<>(getTaskList());
            allTasks.addAll(getEpicList());
            allTasks.addAll(getSubtaskList());

            for (Task task : allTasks) {
                writer.write(toString(task) + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    public String toString(Task task) {
        String result = task.getId() + "," + task.getType() + ","
                + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + ",";
        if (task instanceof Subtask) {
            result += ((Subtask) task).getEpic().getId();
        }
        return result;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager result = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = result.fromString(line);
                if (task instanceof Epic) {
                    result.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    result.subtasks.put(task.getId(), (Subtask) task);
                    Epic epic = result.epics.get(((Subtask) task).getEpic().getId());
                    epic.addSubtask((Subtask) task);
                } else {
                    result.tasks.put(task.getId(), task);
                }
                if (task.getId() > result.id) {
                    result.id = task.getId();
                }
            }
            result.id++;
            return result;
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    private Task fromString(String value) {
        String[] taskParameters = value.split(",");
        Task task;
        switch (taskParameters[1]) {
            case "TASK":
                task = new Task(taskParameters[2], taskParameters[4]);
                task.setId(Integer.parseInt(taskParameters[0]));
                task.setStatus(Status.valueOf(taskParameters[3]));
                break;
            case "EPIC":
                task = new Epic(taskParameters[2], taskParameters[4]);
                task.setId(Integer.parseInt(taskParameters[0]));
                break;
            default:
                Epic epic = epics.get(Integer.parseInt(taskParameters[5]));
                task = new Subtask(taskParameters[2], taskParameters[4], epic);
                task.setId(Integer.parseInt(taskParameters[0]));
                task.setStatus(Status.valueOf(taskParameters[3]));
        }
        return task;
    }

    public File getFile() {
        return file;
    }

    public static void main(String[] args) {
        File firstManagerTasks = new File("src" + File.separator + "ru" + File.separator + "practicum"
                + File.separator + "resources" + File.separator + "firstManagerTasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(firstManagerTasks);

        Task task1 = new Task("Турка", "Помыть турку");
        manager.addTask(task1);
        Task task2 = new Task("Кофе", "Сварить кофе");
        manager.addTask(task2);

        Epic epic1 = new Epic("Ремонт", "Сделать ремонт в комнате");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Старые обои", "Удалить старые обои", epic1);
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Новые обои", "Поклеить новые обои", epic1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Чистая машина", "Помыть машину");
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("запись", "записаться на мойку", epic2);
        manager.addSubtask(subtask3);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        FileBackedTaskManager newManager = loadFromFile(firstManagerTasks);

        System.out.println("Task-и оригинала: ");
        System.out.println(manager.getTaskList());
        System.out.println("Task-и нового менеджера: ");
        System.out.println(newManager.getTaskList());
        System.out.println();

        System.out.println("Epic-и оригинала: ");
        System.out.println(manager.getEpicList());
        System.out.println("Epic-и нового менеджера: ");
        System.out.println(newManager.getEpicList());
        System.out.println();

        System.out.println("Subtask-и оригинала: ");
        System.out.println(manager.getSubtaskList());
        System.out.println("Subtask-и нового менеджера: ");
        System.out.println(newManager.getSubtaskList());
        System.out.println();
    }
}
