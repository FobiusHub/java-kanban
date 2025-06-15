package ru.practicum.model;

public class Subtask extends Task implements Cloneable {
    /*
    Из-за того, что в Subtask хранится ссылка на Epic, а в Epic ссылки на его Subtask'и
    метод toJson работает некорректно,
    поэтому поле epic помечено как transient, чтобы оно не учитывалось при сериализации.
    Поле epicId добавлено для идентификации epic сабтаски при десериализации.
    */
    private transient Epic epic;
    private int epicId;

    private Subtask() {
        id = -1;
        status = Status.NEW;
        type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, Epic epic) {
        this();
        this.epic = epic;
        epicId = epic.getId();
        this.name = name;
        this.description = description;
    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Subtask{" +
                "id=" + id +
                ", epicName='" + epic.name + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Override
    public Subtask clone() {
        Subtask newSubtask = new Subtask(this.name, this.description, this.epic.clone());
        newSubtask.setStatus(this.status);
        newSubtask.setId(this.id);
        newSubtask.setDuration(this.getDuration());
        newSubtask.setStartTime(this.getStartTime());
        return newSubtask;
    }
}
