package ru.practicum.model;

public class Subtask extends Task implements Cloneable {
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

    @Override
    public Subtask clone() {
        Subtask subtask = new Subtask(this.name, this.description, this.epic.clone());
        return subtask;
    }
}
