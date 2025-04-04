package ru.practicum.manager;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if(task == null)
            return;
        Task historyTask = task.clone();
        historyTask.setId(task.getId());

        if (history.size() >= 10)
            history.remove(0);
        history.add(historyTask);

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
