package repositories;

import models.User;

public interface UserRepository {
    void save(User user);

    User getUserByLogin(String login);
}
