package ru.practicum.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exceptions.AddTaskException;
import ru.practicum.exceptions.UpdateTaskException;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

abstract public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;
    protected TaskManager taskManager;

    BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    //для отправки общего ответа в случае успеха
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    //для отправки ответа в случае, если объект не был найден
    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        //если пользователь обратился к несуществующему ресурсу (например, попытался получить задачу, которой нет) — статус 404 (Not Found);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    //для отправки ответа, если при создании или обновлении задача пересекается с уже существующими
    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        //если добавляемая задача пересекается с существующими — статус 406 (Not Acceptable);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    //Для отправки информации об ошибке
    protected void sendError(HttpExchange exchange, int rCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(rCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected <T extends Task> T fromJsonToTask(HttpExchange exchange, Class<T> taskClass) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return gson.fromJson(body, taskClass);
        }
    }

    protected void sendAll(HttpExchange exchange, Class<? extends Task> taskClass) throws IOException {
        List<? extends Task> tasks;
        if (taskClass == Task.class) {
            tasks = taskManager.getTaskList();
        } else if (taskClass == Epic.class) {
            tasks = taskManager.getEpicList();
        } else {
            tasks = taskManager.getSubtaskList();
        }
        sendText(exchange, gson.toJson(tasks));
    }

    protected void sendById(HttpExchange exchange, String numString, Class<? extends Task> taskClass) throws IOException {
        try {
            int id = Integer.parseInt(numString);
            Optional<? extends Task> task;

            if (taskClass == Task.class) {
                task = taskManager.getTask(id);
            } else if (taskClass == Epic.class) {
                task = taskManager.getEpic(id);
            } else {
                task = taskManager.getSubtask(id);
            }

            if (task.isPresent()) {
                sendText(exchange, gson.toJson(task.get()));
            } else {
                sendNotFound(exchange, "Задача id: " + id + " не существует!");
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange, "Неверный формат id");
        }
    }

    protected <T extends Task> void post(HttpExchange exchange, Class<T> taskClass) throws IOException {
        try {
            T task = fromJsonToTask(exchange, taskClass);
            if (task == null) {
                sendError(exchange, 400, "Некорректный запрос: пустое тело запроса");
                return;
            }
            int id = task.getId();
            Optional<? extends Task> optTask;
            if (taskClass == Task.class) {
                optTask = taskManager.getTask(id);
            } else if (taskClass == Epic.class) {
                optTask = taskManager.getEpic(id);
            } else {
                optTask = taskManager.getSubtask(id);
                //Из-за того, что поле Epic в Subclass transient, при десериализации оно остается null
                //Потому необходимо создать Subtask корректным полем Epic
                task = (T) createSubtaskWithEpic((Subtask) task);
            }

            if (id == -1) {
                int newId;

                if (taskClass == Task.class) {
                    newId = taskManager.addTask(task);
                } else if (taskClass == Epic.class) {
                    newId = taskManager.addEpic((Epic) task);
                } else {
                    newId = taskManager.addSubtask((Subtask) task);
                }

                sendText(exchange, "Задача добавлена, id: " + newId);
            } else if (optTask.isPresent()) {
                if (taskClass == Task.class) {
                    taskManager.updateTask(task);
                } else if (taskClass == Epic.class) {
                    taskManager.updateEpic((Epic) task);
                } else {
                    taskManager.updateSubtask((Subtask) task);
                }

                exchange.sendResponseHeaders(201, 0);
                exchange.close();
            } else {
                sendNotFound(exchange, "Задача id: " + id + " не существует!");
            }
        } catch (AddTaskException | UpdateTaskException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    protected void delete(HttpExchange exchange, String numString, Class<? extends Task> taskClass) throws IOException {
        try {
            int id = Integer.parseInt(numString);
            Optional<? extends Task> task;

            if (taskClass == Task.class) {
                task = taskManager.getTask(id);
            } else if (taskClass == Epic.class) {
                task = taskManager.getEpic(id);
            } else {
                task = taskManager.getSubtask(id);
            }

            if (task.isPresent()) {
                if (taskClass == Task.class) {
                    taskManager.deleteTask(id);
                } else if (taskClass == Epic.class) {
                    taskManager.deleteEpic(id);
                } else {
                    taskManager.deleteSubtask(id);
                }
                exchange.sendResponseHeaders(201, 0);
                exchange.close();
            } else {
                sendNotFound(exchange, "Задача id: " + id + " не существует!");
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange, "Неверный формат id");
        }
    }

    /*
    Метод проверяет корректно ли указан Epic в переданной в запросе Subtask.
    Возвращает Subtask с корректно проинициализированными полями
     */
    private Subtask createSubtaskWithEpic(Subtask otherSubtask) {
        String name = otherSubtask.getName();
        String description = otherSubtask.getDescription();
        Optional<Epic> optionalEpic = taskManager.getEpic(otherSubtask.getEpicId());
        if (optionalEpic.isPresent()) {
            Subtask result = new Subtask(name, description, optionalEpic.get());
            result.setId(otherSubtask.getId());
            result.setStatus(otherSubtask.getStatus());
            result.setStartTime(otherSubtask.getStartTime());
            result.setDuration(otherSubtask.getDuration());
            return result;
        } else {
            throw new AddTaskException("Эпика c id " + otherSubtask.getEpicId() + " не существует!");
        }
    }
}
