import services.TaskService;
import services.Userservice;

public class Main {
    static Userservice userservice = new Userservice();
    static TaskService taskService = new TaskService();

    public static void main(String[] args) {
        userservice.useAuthorisationMenu();
        taskService.actionMenu();
    }
}
