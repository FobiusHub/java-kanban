package ru.practicum.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskHandlerTest {
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
    public void subtasksGetShouldReturnValidListWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        class TaskListTypeToken extends TypeToken<List<Subtask>> {
        }
        List<Subtask> subtaskList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        Subtask expectedTask1 = manager.getSubtaskList().getFirst();
        Subtask expectedTask2 = manager.getSubtaskList().getLast();

        Subtask actualTask1 = subtaskList.getFirst();
        Subtask actualTask2 = subtaskList.getLast();

        assertEquals(expectedTask1.getId(), actualTask1.getId());
        assertEquals(expectedTask1.getEpicId(), actualTask1.getEpicId());
        assertEquals(expectedTask1.getName(), actualTask1.getName());
        assertEquals(expectedTask1.getDescription(), actualTask1.getDescription());
        assertEquals(expectedTask1.getType(), actualTask1.getType());
        assertEquals(expectedTask1.getStatus(), actualTask1.getStatus());
        assertEquals(expectedTask1.getStartTime(), actualTask1.getStartTime());
        assertEquals(expectedTask1.getDuration(), actualTask1.getDuration());

        assertEquals(expectedTask2.getId(), actualTask2.getId());
        assertEquals(expectedTask2.getEpicId(), actualTask2.getEpicId());
        assertEquals(expectedTask2.getName(), actualTask2.getName());
        assertEquals(expectedTask2.getDescription(), actualTask2.getDescription());
        assertEquals(expectedTask2.getType(), actualTask2.getType());
        assertEquals(expectedTask2.getStatus(), actualTask2.getStatus());
        assertEquals(expectedTask2.getStartTime(), actualTask2.getStartTime());
        assertEquals(expectedTask2.getDuration(), actualTask2.getDuration());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void subtasksGetWithIdShouldReturnValidSubtaskWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Subtask expectedTask = manager.getSubtaskList().getFirst();

        Subtask actualTask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getEpicId(), actualTask.getEpicId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertEquals(expectedTask.getType(), actualTask.getType());
        assertEquals(expectedTask.getStatus(), actualTask.getStatus());
        assertEquals(expectedTask.getStartTime(), actualTask.getStartTime());
        assertEquals(expectedTask.getDuration(), actualTask.getDuration());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void subtasksPostShouldCorrectlyAddTaskToManager() throws IOException, InterruptedException {
        initializeTasks(manager);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2", manager.getEpicList().getFirst());
        // конвертируем её в JSON
        String jsonTask = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    public void subtasksDeleteShouldCorrectlyRemoveTaskFromManager() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);


        assertEquals(1, manager.getSubtaskList().size());
        assertEquals("Новые обои", manager.getSubtaskList().getFirst().getName());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldUpdateSubtaskIfIdExist() throws IOException, InterruptedException {
        initializeTasks(manager);
        String jsonTask = gson.toJson(manager.getSubtaskList().getFirst());
        jsonTask = jsonTask.replace("Старые обои", "Штукатурка");

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals("Штукатурка", manager.getSubtaskList().getFirst().getName());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldSendStatus404IfSubtaskNotExist() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldSendStatus406IfSubtaskIsIntersectWithAny() throws IOException, InterruptedException {
        initializeTasks(manager);
        String jsonTask = gson.toJson(manager.getSubtaskList().getFirst());
        jsonTask = jsonTask.replace("01.02.2025 08:00", "03.02.2025 08:00");

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
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
        Epic epic1 = new Epic("Ремонт", "Сделать ремонт в комнате");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Старые обои", "Удалить старые обои", epic1);
        subtask1.setStartTime(LocalDateTime.of(2025, 2, 1, 8, 0));
        subtask1.setDuration(240);
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Новые обои", "Поклеить новые обои", epic1);
        subtask2.setStartTime(LocalDateTime.of(2025, 2, 3, 8, 0));
        subtask2.setDuration(240);
        manager.addSubtask(subtask2);
    }
}