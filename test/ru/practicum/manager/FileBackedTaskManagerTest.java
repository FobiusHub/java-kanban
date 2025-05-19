package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.exceptions.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createTaskManager() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(File.createTempFile("testFile", ".csv"));
        task = new Task("taskName", "description");
        fileBackedTaskManager.addTask(task);
        epic = new Epic("epicName", "description");
        fileBackedTaskManager.addEpic(epic);
        subtask = new Subtask("subtaskName", "description", epic);
        fileBackedTaskManager.addSubtask(subtask);
    }

    //сохранение пустого файла
    @Test
    public void shouldSaveEmptyFileAfterClearTasks() {
        fileBackedTaskManager.clearTasks();
        fileBackedTaskManager.clearEpics();
        fileBackedTaskManager.clearSubtasks();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileBackedTaskManager.getFile()))) {
            //должен сохраниться только заголовок
            Assertions.assertEquals("id,type,name,status,description,epic", reader.readLine());
            //следующая строка должна быть пустой
            Assertions.assertEquals(null, reader.readLine());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    //загрузка пустого файла
    @Test
    public void shouldCorrectlyLoadEmptyFile() throws IOException {
        File file = File.createTempFile("testClearFile", ".csv");
        FileBackedTaskManager testLoadManager = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertEquals(0, testLoadManager.getTaskList().size());
        Assertions.assertEquals(0, testLoadManager.getEpicList().size());
        Assertions.assertEquals(0, testLoadManager.getSubtaskList().size());
    }

    //загрузку нескольких задач
    @Test
    public void shouldCorrectlyLoadTasks() {
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getFile());

        Task newManagerTask = newManager.getTask(0);
        Assertions.assertEquals(task.getId(), newManagerTask.getId());
        Assertions.assertEquals(task.getName(), newManagerTask.getName());
        Assertions.assertEquals(task.getDescription(), newManagerTask.getDescription());
        Assertions.assertEquals(task.getType(), newManagerTask.getType());
        Assertions.assertEquals(task.getStatus(), newManagerTask.getStatus());

        Epic newManagerEpic = newManager.getEpic(1);
        Assertions.assertEquals(epic.getId(), newManagerEpic.getId());
        Assertions.assertEquals(epic.getName(), newManagerEpic.getName());
        Assertions.assertEquals(epic.getDescription(), newManagerEpic.getDescription());
        Assertions.assertEquals(epic.getType(), newManagerEpic.getType());
        Assertions.assertEquals(epic.getStatus(), newManagerEpic.getStatus());
        Assertions.assertEquals(epic.getEpicSubtasks().getFirst(), newManagerEpic.getEpicSubtasks().getFirst());

        Subtask newManagerSubtask = newManager.getSubtask(2);
        Assertions.assertEquals(subtask.getId(), newManagerSubtask.getId());
        Assertions.assertEquals(subtask.getName(), newManagerSubtask.getName());
        Assertions.assertEquals(subtask.getDescription(), newManagerSubtask.getDescription());
        Assertions.assertEquals(subtask.getType(), newManagerSubtask.getType());
        Assertions.assertEquals(subtask.getStatus(), newManagerSubtask.getStatus());
        Assertions.assertEquals(subtask.getEpic().getId(), newManagerSubtask.getEpic().getId());

        //проверим, что после загрузки файла правильно меняется счетчик id
        newManager.addTask(new Task("testIdTask", "testIdDescription"));
        Assertions.assertEquals("testIdTask", newManager.getTask(3).getName());
    }

    @Test
    public void loadedEpicShouldHaveCorrectStatus() {
        subtask.setStatus(Status.DONE);
        fileBackedTaskManager.updateSubtask(subtask);
        //здесь проверяем, что статус эпика действительно изменился
        Assertions.assertEquals(Status.DONE, epic.getStatus());
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getFile());
        //здесь проверяем, что эпик выгружен с корректным статусом
        Assertions.assertEquals(Status.DONE, newManager.getEpic(1).getStatus());
    }
}
