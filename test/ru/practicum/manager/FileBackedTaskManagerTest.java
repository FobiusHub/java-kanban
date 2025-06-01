package ru.practicum.manager;

import org.junit.jupiter.api.Test;
import ru.practicum.exceptions.ManagerLoadException;
import ru.practicum.exceptions.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected void initializeManager() throws IOException {
        taskManager = new FileBackedTaskManager(File.createTempFile("testFile", ".csv"));
    }

    //сохранение пустого файла
    @Test
    public void shouldSaveEmptyFileAfterClearTasks() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        taskManager.clearSubtasks();
        try (BufferedReader reader = new BufferedReader(new FileReader(((FileBackedTaskManager) taskManager).getFile()))) {
            //должен сохраниться только заголовок
            assertEquals("id,type,name,status,description,startTime,duration,epic", reader.readLine());
            //следующая строка должна быть пустой
            assertNull(reader.readLine());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    //загрузка пустого файла
    @Test
    public void shouldCorrectlyLoadEmptyFile() throws IOException {
        File file = File.createTempFile("testClearFile", ".csv");
        FileBackedTaskManager testLoadManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(0, testLoadManager.getTaskList().size());
        assertEquals(0, testLoadManager.getEpicList().size());
        assertEquals(0, testLoadManager.getSubtaskList().size());
    }

    //загрузку нескольких задач
    @Test
    public void shouldCorrectlyLoadTasks() {
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(((FileBackedTaskManager) taskManager).getFile());

        Task newManagerTask = newManager.getTask(0).get();
        assertEquals(task.getId(), newManagerTask.getId());
        assertEquals(task.getName(), newManagerTask.getName());
        assertEquals(task.getDescription(), newManagerTask.getDescription());
        assertEquals(task.getType(), newManagerTask.getType());
        assertEquals(task.getStatus(), newManagerTask.getStatus());
        assertEquals(task.getStartTime(), newManagerTask.getStartTime());
        assertEquals(task.getDuration(), newManagerTask.getDuration());
        assertTrue(newManager.prioritizedTasks.contains(task));

        Epic newManagerEpic = newManager.getEpic(1).get();
        assertEquals(epic.getId(), newManagerEpic.getId());
        assertEquals(epic.getName(), newManagerEpic.getName());
        assertEquals(epic.getDescription(), newManagerEpic.getDescription());
        assertEquals(epic.getType(), newManagerEpic.getType());
        assertEquals(epic.getStatus(), newManagerEpic.getStatus());
        assertEquals(epic.getEpicSubtasks().getFirst(), newManagerEpic.getEpicSubtasks().getFirst());
        assertEquals(epic.getStartTime(), newManagerEpic.getStartTime());
        assertEquals(epic.getDuration(), newManagerEpic.getDuration());
        assertEquals(epic.getEndTime(), newManagerEpic.getEndTime());

        Subtask newManagerSubtask = newManager.getSubtask(2).get();
        assertEquals(subtask.getId(), newManagerSubtask.getId());
        assertEquals(subtask.getName(), newManagerSubtask.getName());
        assertEquals(subtask.getDescription(), newManagerSubtask.getDescription());
        assertEquals(subtask.getType(), newManagerSubtask.getType());
        assertEquals(subtask.getStatus(), newManagerSubtask.getStatus());
        assertEquals(subtask.getEpic().getId(), newManagerSubtask.getEpic().getId());
        assertEquals(subtask.getStartTime(), newManagerSubtask.getStartTime());
        assertEquals(subtask.getDuration(), newManagerSubtask.getDuration());
        assertTrue(newManager.prioritizedTasks.contains(subtask));

        //проверим, правильно ли выгружается Epic с пустыми полями duration и startTime
        Epic newManagerEpic2 = newManager.getEpic(3).get();
        assertEquals(epic2.getStartTime(), newManagerEpic2.getStartTime());
        assertEquals(epic2.getDuration(), newManagerEpic2.getDuration());

        //проверим, что после загрузки файла правильно меняется счетчик id
        newManager.addTask(new Task("testIdTask", "testIdDescription"));
        assertEquals("testIdTask", newManager.getTask(4).get().getName());
    }

    @Test
    public void loadedEpicShouldHaveCorrectStatus() {
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        //здесь проверяем, что статус эпика действительно изменился
        assertEquals(Status.DONE, epic.getStatus());
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(((FileBackedTaskManager) taskManager).getFile());
        //здесь проверяем, что эпик выгружен с корректным статусом
        assertEquals(Status.DONE, newManager.getEpic(1).get().getStatus());
    }

    @Test
    public void saveShouldThrowExceptionIfFilePathNotExist() {
        taskManager = new FileBackedTaskManager(new File("somepath/somefile.csv"));
        assertThrows(ManagerSaveException.class, () -> {
            taskManager.addTask(new Task("name", "description"));
        });
    }

    @Test
    public void loadShouldThrowExceptionIfFilePathNotExist() {
        assertThrows(ManagerLoadException.class, () -> {
            taskManager = FileBackedTaskManager.loadFromFile(new File("somepath/somefile.csv"));
        });
    }

}
