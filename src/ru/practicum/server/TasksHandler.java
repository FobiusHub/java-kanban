package ru.practicum.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.net.URI;


public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitRequestURI = path.split("/");
        int requestLength = splitRequestURI.length;
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                switch (requestLength) {
                    case 2 -> sendAll(exchange, Task.class);
                    case 3 -> sendById(exchange, splitRequestURI[2], Task.class);
                    default -> sendNotFound(exchange, "Неверный запрос");
                }
                break;
            case "POST":
                if (requestLength == 2) {
                    post(exchange, Task.class);
                } else {
                    sendNotFound(exchange, "Неверный запрос");
                }
                break;
            case "DELETE":
                if (requestLength == 3) {
                    delete(exchange, splitRequestURI[2], Task.class);
                } else {
                    sendNotFound(exchange, "Неверный запрос");
                }
                break;
            default:
                exchange.sendResponseHeaders(500, 0);
                exchange.close();
        }
    }
}