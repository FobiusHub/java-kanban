package ru.practicum.model;

public class Subtask extends Task {
    private final Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Subtask{" +
                "id=" + id +
                ", epicName='" + epic.name + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
