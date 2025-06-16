package ru.practicum.server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicHandlerTest {
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
    public void epicsGetShouldReturnValidListWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        class TaskListTypeToken extends TypeToken<List<Epic>> {
        }
        List<Epic> taskList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        Epic expectedTask1 = manager.getEpicList().getFirst();
        Epic actualTask1 = taskList.getFirst();

        assertEquals(expectedTask1.getId(), actualTask1.getId());
        assertEquals(expectedTask1.getEpicSubtasks(), actualTask1.getEpicSubtasks());
        assertEquals(expectedTask1.getName(), actualTask1.getName());
        assertEquals(expectedTask1.getDescription(), actualTask1.getDescription());
        assertEquals(expectedTask1.getType(), actualTask1.getType());
        assertEquals(expectedTask1.getStatus(), actualTask1.getStatus());
        assertEquals(expectedTask1.getStartTime(), actualTask1.getStartTime());
        assertEquals(expectedTask1.getDuration(), actualTask1.getDuration());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void epicsGetWithIdShouldReturnValidEpicWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Epic expectedTask = manager.getEpicList().getFirst();
        Epic actualTask = gson.fromJson(response.body(), Epic.class);

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
    public void epicsPostShouldCorrectlyAddTaskToManager() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        String jsonTask = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpicList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void epicsDeleteShouldCorrectlyRemoveTaskFromManager() throws IOException, InterruptedException {
        initializeTasks(manager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);


        assertEquals(0, manager.getEpicList().size());
        assertEquals(0, manager.getSubtaskList().size());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldUpdateEpicIfIdExist() throws IOException, InterruptedException {
        Epic epic = new Epic("EpicName", "EpicDescription");
        manager.addEpic(epic);
        String jsonTask = gson.toJson(manager.getEpicList().getFirst());
        jsonTask = jsonTask.replace("EpicName", "NameOfEpic");

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals("NameOfEpic", manager.getEpicList().getFirst().getName());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldSendStatus404IfEpicNotExist() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
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