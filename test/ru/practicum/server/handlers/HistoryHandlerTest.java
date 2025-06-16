package ru.practicum.server.handlers;

import com.google.gson.Gson;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
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
    public void historyGetShouldReturnValidListWithCorrectFields() throws IOException, InterruptedException {
        initializeTasks(manager);
        String jsonTasksList = gson.toJson(manager.getSubtaskList());
        manager.getSubtask(1);
        manager.getSubtask(2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(jsonTasksList, response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void historyGetWithIncorrectPathShouldReturn404() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
    }

    @Test
    public void historyWithIncorrectRequestShouldReturn406() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(500, response.statusCode());
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
