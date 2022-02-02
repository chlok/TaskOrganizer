package services;

import models.Task;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import repositories.TaskRepository;
import repositories.TaskRepositoryJDBCImpl;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

public class TaskService {
    private ConsoleReader consoleReader;
    private TaskRepository repository;

    private void showMainMenu() {
        System.out.println("Please, choose action from the following list:");
        System.out.println("1.Create a new task");
        System.out.println("2.Create an undertask");
        System.out.println("3.Get all the tasks to do");
        System.out.println("4. Mark the task as done");
        System.out.println("5.Exit");
    }

    /**
     * this method handles all user actions and defines all logic of the app
     */
    public void actionMenu() {
        Properties properties = new PropertiesSupplier().getProperties();
        String URL = properties.getProperty("url");
        String name = properties.getProperty("username");
        String password = properties.getProperty("password");
        DataSource dataSource = new DriverManagerDataSource(URL, name, password);
        repository = new TaskRepositoryJDBCImpl(dataSource);
        Thread thread = new TaskChecker();
        consoleReader = new ConsoleReader(System.in);
        boolean isWorking = true;
        while (isWorking) {
            showMainMenu();
            int answer = consoleReader.readInteger();
            if (answer == 0) {
                System.out.println("your input is not correct! try again!");
                continue;
            }
            switch (answer) {
                case 1 -> {
                    Task task = createNewTask();
                    repository.save(task);
                }
                case 2 -> createSubTask();
                case 3 -> getAllUndoneTasks();
                case 4 -> markTaskAsDone();
                case 5 -> {
                    System.out.println("Thanks for using the Task manager");
                    isWorking = false;
                }
                default -> System.out.println("Entered number is not correct! please try again!");
            }
        }
    }

    /**
     * method gets task id from user and marks it as done
     */
    private void markTaskAsDone() {
        System.out.println("enter an id of the task");
        int id = consoleReader.readInteger();
        Task task;
        try {
            task = repository.getTaskById(id);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Such task doesn't exist!");
            return;
        }
        List<Task> taskList = repository.getAllByParentId(task);
        if (taskList.size() == 0) {
            repository.markTaskAsDone(id);
            System.out.println("this task is done!");
        } else {
            System.out.println("you have some undertasks for this task:");
            for (Task taskToDo : taskList) {
                System.out.println(taskToDo);
            }
        }
    }

    /**
     * method gets all undone tasks
     */
    private void getAllUndoneTasks() {
        List<Task> taskList = repository.getAllToDo();
        if (taskList.size() == 0) {
            System.out.println("there is no undone tasks!");
            return;
        }
        for (Task taskToDo : taskList) {
            System.out.println(taskToDo);
        }
    }


    /**
     * this creates new tasks as subtasks of any existing task
     */
    private void createSubTask() {
        System.out.println("enter an id of the task");
        int id = consoleReader.readInteger();
        Task parentTask;
        try {
            parentTask = repository.getTaskById(id);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Such task doesn't exist!");
            return;
        }
        if (parentTask.getDone()) {
            System.out.println("this task has already done!");
            return;
        }
        Task subTask = createNewTask();
        subTask.setParent(parentTask);
        repository.save(subTask);
    }

    /**
     * method creates new task by user instructions
     *
     * @return new task
     */
    private Task createNewTask() {
        System.out.println("enter the description of the task: ");
        String description = consoleReader.readString();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("enter the deadline details");
        LocalDateTime deadlineDate = consoleReader.readLocaleDateTime();
        System.out.println("the task is created");
        return new Task(description, now, deadlineDate);
    }

    /**
     * this class is used for checking all deadlines of undone tasks in separate thread.
     * if any of them will occur in 1 hour this shows the information in app
     */
    class TaskChecker extends Thread {

        public TaskChecker() {
            super();
            this.setDaemon(true);
            this.start();
        }

        @Override
        public void run() {
            while (true) {
                List<Task> taskList = repository.getAllToDo();
                for (Task task : taskList) {
                    long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                            task.getDeadline());
                    if (seconds == 3600) {
                        System.out.println("there is only one hour for doing the following task:");
                        System.out.println(task);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
