import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
    }

    public void deleteSubtask(Subtask subtask){
        subtasks.remove(subtask);
        updateStatus();
    }

    public void clearSubtasks() {
        subtasks.clear();
        updateStatus();
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("Ручная установка статуса для Epic недоступна!");
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            super.setStatus(Status.NEW);
            return;
        }
        boolean isNew = true;
        boolean isDone = true;

        for (Subtask task : subtasks) {
            if (task.getStatus() == Status.IN_PROGRESS) {
                super.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (task.getStatus() != Status.DONE)
                isDone = false;
            if (task.getStatus() != Status.NEW)
                isNew = false;
        }

        if (isNew)
            super.setStatus(Status.NEW);
        else if (isDone)
            super.setStatus(Status.DONE);
        else
            super.setStatus(Status.IN_PROGRESS);
    }
}
