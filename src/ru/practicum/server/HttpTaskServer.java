package ru.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.server.adapters.DurationAdapter;
import ru.practicum.server.adapters.LocalDateTimeAdapter;
import ru.practicum.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager = manager;
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен.");
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer taskServer = new HttpTaskServer(new InMemoryTaskManager());
        setTasks(taskServer.taskManager);
        taskServer.start();
    }

    //для тестирования в Insomnia
    private static void setTasks(TaskManager manager) {
        Task task1 = new Task("Турка", "Помыть турку");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 8, 0));
        task1.setDuration(20);
        manager.addTask(task1);
        Task task2 = new Task("Кофе", "Сварить кофе");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 8, 20));
        task2.setDuration(10);
        manager.addTask(task2);

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

        Epic epic2 = new Epic("Чистая машина", "Помыть машину");
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("запись", "записаться на мойку", epic2);
        subtask3.setStartTime(LocalDateTime.of(2025, 2, 4, 8, 0));
        subtask3.setDuration(240);
        manager.addSubtask(subtask3);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        Epic epic3 = new Epic("EpicName", "EpicDescription");
        manager.addEpic(epic3);
    }
}
