package repositories;

import models.Task;

import java.util.List;

public interface TaskRepository {
    void save(Task task);


    List<Task> getAllToDo();

    List<Task> getAllByParentId(Task task);

    Task getTaskById(int id);

    void markTaskAsDone(int id);
}
