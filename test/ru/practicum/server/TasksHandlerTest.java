package ru.practicum.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksHandlerTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        gson = server.getGson();
        server.start();
    }

    @Test
    public void tasksGetShouldReturnValidListWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        class TaskListTypeToken extends TypeToken<List<Task>> {
        }
        List<Task> taskList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        Task expectedTask1 = manager.getTaskList().getFirst();
        Task expectedTask2 = manager.getTaskList().getLast();

        Task actualTask1 = taskList.getFirst();
        Task actualTask2 = taskList.getLast();

        assertEquals(expectedTask1.getId(), actualTask1.getId());
        assertEquals(expectedTask1.getName(), actualTask1.getName());
        assertEquals(expectedTask1.getDescription(), actualTask1.getDescription());
        assertEquals(expectedTask1.getType(), actualTask1.getType());
        assertEquals(expectedTask1.getStatus(), actualTask1.getStatus());
        assertEquals(expectedTask1.getStartTime(), actualTask1.getStartTime());
        assertEquals(expectedTask1.getDuration(), actualTask1.getDuration());

        assertEquals(expectedTask2.getId(), actualTask2.getId());
        assertEquals(expectedTask2.getName(), actualTask2.getName());
        assertEquals(expectedTask2.getDescription(), actualTask2.getDescription());
        assertEquals(expectedTask2.getType(), actualTask2.getType());
        assertEquals(expectedTask2.getStatus(), actualTask2.getStatus());
        assertEquals(expectedTask2.getStartTime(), actualTask2.getStartTime());
        assertEquals(expectedTask2.getDuration(), actualTask2.getDuration());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void tasksGetWithIdShouldReturnValidTaskWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Task expectedTask = manager.getTaskList().getFirst();

        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertEquals(expectedTask.getType(), actualTask.getType());
        assertEquals(expectedTask.getStatus(), actualTask.getStatus());
        assertEquals(expectedTask.getStartTime(), actualTask.getStartTime());
        assertEquals(expectedTask.getDuration(), actualTask.getDuration());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void tasksPostShouldCorrectlyAddTaskToManager() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2");
        // конвертируем её в JSON
        String jsonTask = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void tasksDeleteShouldCorrectlyRemoveTaskFromManager() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);


        assertEquals(1, manager.getTaskList().size());
        assertEquals("Кофе", manager.getTaskList().getFirst().getName());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldUpdateTaskIfIdExist() throws IOException, InterruptedException {
        initializeTasks(manager);
        String parametersToUpdate = "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"name\": \"Суп\",\n" +
                "\t\"description\": \"Сварить борщ\",\n" +
                "\t\"status\": \"NEW\",\n" +
                "\t\"type\": \"TASK\",\n" +
                "\t\"startTime\": \"01.01.2025 08:20\",\n" +
                "\t\"duration\": 10\n" +
                "}";

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(parametersToUpdate))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals("Суп", manager.getTaskList().getLast().getName());
        assertEquals("Сварить борщ", manager.getTaskList().getLast().getDescription());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldSendStatus404IfTaskNotExist() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldSendStatus406IfTaskIsIntersectWithAny() throws IOException, InterruptedException {
        initializeTasks(manager);
        String jsonTask = "{\n" +
                "\t\"name\": \"Суп\",\n" +
                "\t\"description\": \"Сварить борщ\",\n" +
                "\t\"startTime\": \"01.01.2025 08:20\",\n" +
                "\t\"duration\": 10\n" +
                "}";

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(406, response.statusCode());
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    private void initializeTasks(TaskManager manager) {
        Task task1 = new Task("Турка", "Помыть турку");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 8, 0));
        task1.setDuration(20);
        manager.addTask(task1);
        Task task2 = new Task("Кофе", "Сварить кофе");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 8, 20));
        task2.setDuration(10);
        manager.addTask(task2);
    }
}