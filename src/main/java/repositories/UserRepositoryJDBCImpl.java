package repositories;

import models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.crypto.*;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UserRepositoryJDBCImpl implements UserRepository {

    JdbcTemplate jdbcTemplate;
    SecretKey key;

    //language=sql
    private final String SQL_INSERT_USER = "INSERT INTO user VALUES (?, ?)";

    //language=sql
    private final String SQL_GET_USER_BY_LOGIN = "SELECT * FROM user WHERE login=?";

    RowMapper<User> rowMapper = ((rs, rowNum) -> {
        String login = rs.getString("login");
        String password = cryptPassword(-1, rs.getString("password"));
        return new User(login, password);
    });

    public UserRepositoryJDBCImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("AES");
            key = keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method saves the user in the database
     */
    @Override
    public void save(User user) {
        jdbcTemplate.update(SQL_INSERT_USER, user.getLogin(), cryptPassword(1, user.getPassword()));
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

    /**
     * method encryptes and decryptes the password for database
     * @param cryptMode 1 - to encrypt and -1 to decrypt
     * @param password
     * @return encrypted/decrypted password
     */
    String cryptPassword(int cryptMode, String password) {
        StringBuilder cryptedPassword = new StringBuilder();
        for (char ch:password.toCharArray()){
            cryptedPassword.append(ch + cryptMode);
        }
        return cryptedPassword.toString();
    }

}

