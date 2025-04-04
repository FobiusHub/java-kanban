package ru.practicum.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task implements Cloneable {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new HashMap<>();
    }

    public List<Subtask> getEpicSubtasks() {
        List<Subtask> subtaskList = new ArrayList<>(subtasks.values());
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        if(subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            updateStatus();
        }
    }

    public void deleteSubtask(Subtask subtask){
        if(subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.remove(subtask.getId());
            updateStatus();
        }
    }

    public void clearSubtasks() {
        subtasks.clear();
        updateStatus();
    }

    public void updateSubtask(Subtask subtask) {
        if(subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.id, subtask);
            updateStatus();
        }
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("Ручная установка статуса для ru.practicum.model.Epic недоступна!");
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    private void updateStatus() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
            return;
        }
        boolean isNew = true;
        boolean isDone = true;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.status == Status.IN_PROGRESS) {
                status = Status.IN_PROGRESS;
                return;
            }
            if (subtask.status != Status.DONE)
                isDone = false;
            if (subtask.status != Status.NEW)
                isNew = false;
        }

        if (isNew)
            status = Status.NEW;
        else if (isDone)
            status = Status.DONE;
        else
            status = Status.IN_PROGRESS;
    }

    @Override
    public Epic clone() {
        Epic newEpic = new Epic(this.name, this.description);
        newEpic.status = this.status;
        newEpic.setId(this.id);
        for(Subtask subtask : this.subtasks.values()) {
            newEpic.addSubtask(subtask);
        }
        return newEpic;
    }
}
