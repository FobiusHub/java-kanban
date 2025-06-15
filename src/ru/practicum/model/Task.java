package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Cloneable {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected TaskType type;
    protected LocalDateTime startTime;
    protected Duration duration;

    protected Task() {
        id = -1;
        status = Status.NEW;
        type = TaskType.TASK;
    }

    public Task(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }

        Task otherTask = (Task) object;

        return this.id == otherTask.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Task clone() {
        Task newTask = new Task(this.name, this.description);
        newTask.setId(this.id);
        newTask.setStatus(this.status);
        newTask.setDuration(this.getDuration());
        newTask.setStartTime(this.getStartTime());
        return newTask;
    }

    public TaskType getType() {
        return type;
    }

    public long getDuration() {
        if (duration == null) {
            return 0;
        }
        return duration.toMinutes();
    }

    public void setDuration(long minutes) {
        duration = Duration.ofMinutes(minutes);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime newStartTime) {
        startTime = newStartTime;
    }

    public LocalDateTime getEndTime() {
        return duration == null ? startTime : startTime.plus(duration);
    }
}
