package models;

import java.time.LocalDateTime;

/**
 * this class is fundamental in the app and describes general quantities of our task
 */
public class Task {
    Integer id;
    String description;
    LocalDateTime creationDate;
    LocalDateTime deadline;
    Boolean isDone;
    Task parent;

    public Task(String description, LocalDateTime creationDate, LocalDateTime deadline) {
        this.description = description;
        this.creationDate = creationDate;
        this.deadline = deadline;
    }

    public Task(Integer id, String description, LocalDateTime creationDate, LocalDateTime deadline, Boolean isDone, Task parent) {
        this.id = id;
        this.description = description;
        this.creationDate = creationDate;
        this.deadline = deadline;
        this.isDone = isDone;
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Task getParent() {
        return parent;
    }

    public Boolean getDone() {
        return isDone;
    }

    @Override
    public String toString() {
        return "Task №" +
                +id +
                ", description:'" + description + '\'' +
                ", creationDate:" + creationDate +
                ", deadline:" + deadline +
                ", status:" + (isDone ? "completed" : "in process") +
                (parent==null?"": ", parent task: №" + parent.getId());
    }
}
