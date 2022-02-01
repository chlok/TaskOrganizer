package services;

import models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import repositories.UserRepository;
import repositories.UserRepositoryJDBCImpl;

import javax.sql.DataSource;
import java.util.InputMismatchException;
import java.util.Properties;

public class Userservice {
    private UserRepository repository;
    ConsoleReader consoleReader;
    private final String propertiesRoot = "src/main/resources/application.properties";

    void showAuthorisationMenu() {
        System.out.println("1. Registration");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.println(" Please, enter the number of the action you want to choose");
        System.out.print("Your number: ");
    }

    /**
     * method handles all user actions during his registration and authorisation
     */
    public void useAuthorisationMenu() {
        consoleReader = new ConsoleReader(System.in);
        int answer;
        Properties properties = PropertiesSupplier.getProperties(propertiesRoot);
        String URL = properties.getProperty("url");
        String name = properties.getProperty("username");
        String password = properties.getProperty("password");
        DataSource dataSource = new DriverManagerDataSource(URL, name, password);
        repository = new UserRepositoryJDBCImpl(dataSource);
        boolean isWorking = true;
        while (isWorking) {
            showAuthorisationMenu();
            try {
                answer = consoleReader.readInteger();
            } catch (InputMismatchException e) {
                System.err.println("your input is not correct! You need to enter a number!!!");
                continue;
            }
            switch (answer) {
                case 1:
                    makeRegistration();
                    break;
                case 2:
                    if (checkUserAutorisation()) {
                        System.out.println("Authorisation is successful!");
                        return;
                    }
                case 3:
                    System.out.println("Thanks for using our Task Manager!");
                    isWorking = false;
                default:
                    System.out.println("your number is not correct! Please try again!");
            }
        }
    }

    /**
     * method is responsible for user registration
     */
    private void makeRegistration() {
        System.out.print("Enter your login");
        String login = consoleReader.readString();
        try {
            User user = repository.getUserByLogin(login);
            System.out.println("Such login already exists!");
            return;
        } catch (EmptyResultDataAccessException e) {
        }
        System.out.print("Enter your password");
        String password = consoleReader.readString();
        repository.save(new User(login, password));
        System.out.println("Registration is successful");
        useAuthorisationMenu();
    }

    /**
     * method checks that password is correct for chosen login
     *
     */
    boolean checkUserAutorisation() {
        System.out.print("Enter your login");
        String login = consoleReader.readString();

        try {
            User user = repository.getUserByLogin(login);
        } catch (EmptyResultDataAccessException e) {
            System.err.println("such login doesn't exist!!!");
            System.out.println("choose another login: ");

        }
        System.out.print("Enter your password");
        String password = consoleReader.readString();
        User user = new User(login, password);
        if (user.getPassword().equals(password)) {
            return true;
        } else {
            System.err.println("your password is incorrect!");
            return false;
        }
    }
}
