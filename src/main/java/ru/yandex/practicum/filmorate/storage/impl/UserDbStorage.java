package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidateUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

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

    @Override
    public User createUser(User user) {
        ValidateUser.validateCreateUser(user);
        if (jdbcTemplate.query("SELECT id from users",
                (rs, rowNum) -> rs.getLong("id")).contains(user.getId())) {
            log.error("пользователь с таким номером уже существует");
            throw new ValidationException("пользователь с таким номером уже существует");
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
        ValidateUser.validateUpdateUser(user);
        String sql = "UPDATE users SET name = ?, login = ?, birthday = ?, email = ? where id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(),
                user.getBirthday(), user.getEmail(), user.getId());
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
        ValidateUser.validateIdUser(id);
        ValidateUser.validateIdUser(friendId);
        String sql = "INSERT INTO friendship VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public List<User> getListFriends(Long id) {
        ValidateUser.validateIdUser(id);
        String sql = "SELECT * from friendship AS f " +
                "INNER JOIN users AS u ON f.friend_id = u.id where f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        ValidateUser.validateIdUser(userId);
        ValidateUser.validateIdUser(friendId);
        String sql = "DELETE FROM friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getListCommonFriends(long id, long otherId) {
        ValidateUser.validateIdUser(id);
        ValidateUser.validateIdUser(otherId);
        String sql = "SELECT DISTINCT u.email," +
                "u.login," +
                "u.name," +
                "u.birthday, " +
                "u.id " +
                " from friendship AS f " +
                "INNER JOIN users AS u ON f.friend_id = u.id " +
                "WHERE (f.user_id = ? or f.user_id = ?) and (f.friend_id != ? and f.friend_id != ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId, id, otherId);
    }

    @Override
    public Optional<User> getUserById(long id) {
        ValidateUser.validateIdUser(id);
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

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("email"), rs.getString("login"),
                rs.getString("name"), LocalDate.parse(rs.getString(("birthday"))));
        user.setId(rs.getLong("id"));
        return user;
    }
}
