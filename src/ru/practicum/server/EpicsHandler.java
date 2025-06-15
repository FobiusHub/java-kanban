package ru.practicum.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    EpicsHandler(TaskManager taskManager, Gson gson) {
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
                    case 2 -> sendAll(exchange, Epic.class);
                    case 3 -> sendById(exchange, splitRequestURI[2], Epic.class);
                    case 4 -> sendEpicSubtasks(exchange, splitRequestURI[2], splitRequestURI[3]);
                    default -> sendNotFound(exchange, "Неверный запрос");
                }
                break;
            case "POST":
                if (requestLength == 2) {
                    post(exchange, Epic.class);
                } else {
                    sendNotFound(exchange, "Неверный запрос");
                }
                break;
            case "DELETE":
                if (requestLength == 3) {
                    delete(exchange, splitRequestURI[2], Epic.class);
                } else {
                    sendNotFound(exchange, "Неверный запрос");
                }
                break;
            default:
                exchange.sendResponseHeaders(500, 0);
                exchange.close();
        }
    }

    private void sendEpicSubtasks(HttpExchange exchange, String numString, String subPath) throws IOException {
        if (!subPath.equals("subtasks")) {
            sendNotFound(exchange, "Неверный запрос");
            return;
        }
        try {
            int id = Integer.parseInt(numString);
            Optional<Epic> epic = taskManager.getEpic(id);
            if (epic.isPresent()) {
                List<Subtask> subtasks = epic.get().getEpicSubtasks();
                sendText(exchange, gson.toJson(subtasks));
            } else {
                sendNotFound(exchange, "Epic с id " + id + " не существует!");
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange, "Неверный формат id");
        }
    }
}