package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    int currentId = 1;

    public int getUniqueID() {
        return currentId++;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("email"), rs.getString("login"),
                rs.getString("name"), LocalDate.parse(rs.getString(("birthday"))));
        user.setId(rs.getLong("id"));
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        if (id < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    LocalDate.parse(userRows.getString("birthday"))

            );
            user.setId(id);
            return Optional.of(user);
        }
        return null;
    }

    @Override
    public User createUser(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")
                || user.getLogin().isEmpty()) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        String sql = "INSERT INTO users VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, currentId, user.getName(), user.getLogin(),
                user.getBirthday(), user.getEmail());
        user.setId(currentId);
        getUniqueID();
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        String sql = "UPDATE users SET name = ?, login = ?, birthday = ?, email = ? where id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(),
                user.getBirthday(), user.getEmail(), user.getId());
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
        if (id < 0 || friendId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        String sql = "INSERT INTO friendship VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public List<User> getListFriends(Long id) {
        if (id < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        String sql = "SELECT * from friendship AS f " +
                "INNER JOIN users AS u ON f.friend_id = u.id where f.user_id = ?";
        return jdbcTemplate.query(sql, new RowMapper<User>() {

            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        LocalDate.parse(rs.getString("birthday")));
                user.setId(rs.getLong("id"));
                return user;
            }
        }, id);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        if (userId < 0 || friendId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        String sql = "DELETE FROM friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getListCommonFriends(long id, long otherId) {
        String sql = "SELECT DISTINCT u.email," +
                "u.login," +
                "u.name," +
                "u.birthday, " +
                "u.id " +
                " from friendship AS f " +
                "INNER JOIN users AS u ON f.friend_id = u.id " +
                "WHERE (f.user_id = ? or f.user_id = ?) and (f.friend_id != ? and f.friend_id != ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    LocalDate.parse(rs.getString("birthday")));
            user.setId(rs.getLong("id"));
            return user;
        }, id, otherId, id, otherId);
    }
}
