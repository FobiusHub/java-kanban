package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task implements Cloneable {
    private final HashMap<Integer, Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new HashMap<>();
        type = TaskType.EPIC;
    }

    public List<Subtask> getEpicSubtasks() {
        List<Subtask> subtaskList = new ArrayList<>(subtasks.values());
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            updateStatus();
            updateTimeBounds();
            updateDuration();
        }
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.remove(subtask.getId());
            updateStatus();
            updateTimeBounds();
            updateDuration();
        }
    }

    public void clearSubtasks() {
        subtasks.clear();
        updateStatus();
        updateTimeBounds();
        updateDuration();
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.id, subtask);
            updateStatus();
            updateTimeBounds();
            updateDuration();
        }
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("Ручная установка статуса для ru.practicum.model.Epic недоступна!");
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
            if (subtask.status != Status.DONE) {
                isDone = false;
            }
            if (subtask.status != Status.NEW) {
                isNew = false;
            }
        }

        if (isNew) {
            status = Status.NEW;
        } else if (isDone) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    @Override
    public void setDuration(long minutes) {
        System.out.println("Ручная установка продолжительности для ru.practicum.model.Epic недоступна!");
    }

    private void updateDuration() {
        long durationSum = 0;

        if (!(subtasks.isEmpty())) {
            for (Subtask subtask : subtasks.values()) {
                durationSum += subtask.getDuration();
            }
        }

        duration = Duration.ofMinutes(durationSum);
    }

    @Override
    public void setStartTime(LocalDateTime newStartTime) {
        System.out.println("Ручная установка времени начала для ru.practicum.model.Epic недоступна!");
    }

    private void updateTimeBounds() {
        if (subtasks.isEmpty()) {
            startTime = null;
            return;
        }

        List<Subtask> sortedSubtasks = subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .toList();

        startTime = sortedSubtasks.isEmpty() ? null : sortedSubtasks.getFirst().getStartTime();
        endTime = sortedSubtasks.isEmpty() ? null : sortedSubtasks.getLast().getEndTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Epic clone() {
        Epic newEpic = new Epic(this.name, this.description);
        newEpic.status = this.status;
        newEpic.setId(this.id);
        for (Subtask subtask : this.subtasks.values()) {
            newEpic.addSubtask(subtask);
        }
        newEpic.duration = Duration.ofMinutes(this.getDuration());
        newEpic.startTime = this.getStartTime();
        return newEpic;
    }
}
