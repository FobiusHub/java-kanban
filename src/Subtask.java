public class Subtask extends Task {
    private Epic epic;

    Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        epic.addSubtask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        epic.updateStatus();
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", epicName='" + epic.getName() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
