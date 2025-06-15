package ru.practicum.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitRequestURI = path.split("/");
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            if (splitRequestURI.length == 2) {
                List<Task> history = taskManager.getHistory();
                sendText(exchange, gson.toJson(history));
            } else {
                sendNotFound(exchange, "Неверный запрос");
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }
}