package repositories;

import models.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskRepositoryJDBCImpl implements TaskRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * this object defines the way we use for transformation of database row into Task-object
     */
    private final RowMapper<Task> rowMapper = (row, rowNumber) -> {
        int id = row.getInt("task_id");
        String description = row.getString("description");
        String creationDateString = row.getString("creation_date");
        String deadlineDateString = row.getString("deadline");
        LocalDateTime deadlineDate = parseString(deadlineDateString);
        LocalDateTime creationDate = parseString(creationDateString);
        Boolean isDone = row.getBoolean("done_mark");
        int parentId = row.getInt("parent_task_id");
        Task parentTask = null;
        if (parentId != 0) {
            parentTask = this.getTaskById(parentId);
        }
        return new Task(id, description, creationDate, deadlineDate, isDone, parentTask);
    };

    //language=sql
    private final static String SQL_INSERT = "insert into task(description, creation_date, deadline, parent_task_id) values (?, ?, ?, ?)";

    //language=sql
    private final static String SELECT_FROM_TASK_WHERE_DONE_MARK_0 = "select * from  task where done_mark = 0 order by deadline";

    //language=sql
    private final static String SELECT_FROM_TASK_BY_PARENT_ID = "select * from  task where done_mark=0 and parent_task_id = ?";


    //language=sql
    private final static String SELECT_FROM_TASK_BY_ID = "select * from  task where task_id = ?";

    //language=sql
    private final static String UPDATE_TASK_SET_TASK_DONE = "update task set done_mark = 1 where task_id=?";

    public TaskRepositoryJDBCImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * this method saves the object in the database
     *
     * @param task - object we want to save
     */
    @Override
    public void save(Task task) {
        jdbcTemplate.update(SQL_INSERT, task.getDescription(), task.getCreationDate(), task.getDeadline(),
                task.getParent() == null ? null : task.getParent().getId());
    }

    /**
     * this method gets all undone tasks from database sorted by deadline
     *
     * @return list of the tasks
     */
    @Override
    public List<Task> getAllToDo() {
        return jdbcTemplate.query(SELECT_FROM_TASK_WHERE_DONE_MARK_0, rowMapper);
    }

    /**
     * this method gets all subtasks of the task
     *
     * @param task - for this task we search all undertasks
     * @return list of the undertasks
     */
    @Override
    public List<Task> getAllByParentId(Task task) {
        return jdbcTemplate.query(SELECT_FROM_TASK_BY_PARENT_ID, rowMapper, task.getId());
    }

    /**
     * this searches task by its id
     *
     * @param id
     * @return
     */
    @Override
    public Task getTaskById(int id) {
        return jdbcTemplate.queryForObject(SELECT_FROM_TASK_BY_ID, rowMapper, id);
    }

    /**
     * this marks task as done
     *
     * @param id - id of the task we want to mark
     */
    @Override
    public void markTaskAsDone(int id) {
        jdbcTemplate.update(UPDATE_TASK_SET_TASK_DONE, id);
    }

    /**
     * this method is used in rowMapper to parse the String-format date into LocalDateTime
     *
     * @param string - string from database which defines the date
     * @return - date and time in LocalDate format
     */
    LocalDateTime parseString(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(string, formatter);
    }
}
