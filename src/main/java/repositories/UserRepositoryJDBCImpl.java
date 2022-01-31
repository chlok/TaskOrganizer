package repositories;

import models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

public class UserRepositoryJDBCImpl implements UserRepository {

    JdbcTemplate jdbcTemplate;

    //language=sql
    private final String SQL_INSERT_USER = "INSERT INTO user VALUES (?, ?)";

    //language=sql
    private final String SQL_GET_USER_BY_LOGIN = "SELECT * FROM user WHERE login=?";

    RowMapper<User> rowMapper = ((rs, rowNum) -> {
        String login = rs.getString("login");
        String password = rs.getString("password");
        return new User(login, password);
    });

    public UserRepositoryJDBCImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * this method saves the user in the database
     */
    @Override
    public void save(User user) {
        jdbcTemplate.update(SQL_INSERT_USER, user.getLogin(), user.getPassword());
    }

    /**
     * this method takes User from the database by its login
     *
     * @param login - login of the user
     * @return user
     */
    @Override
    public User getUserByLogin(String login) {
        return jdbcTemplate.queryForObject(SQL_GET_USER_BY_LOGIN, rowMapper, login);
    }
}
