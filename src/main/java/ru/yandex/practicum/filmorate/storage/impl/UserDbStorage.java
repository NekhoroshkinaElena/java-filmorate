package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidateUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
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
        String sql = "SELECT * FROM users";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return makeUser(rowSet);

    }

    @Override
    public User createUser(User user) {
        ValidateUser.validateCreateUser(user);
        if (jdbcTemplate.query("SELECT id FROM users",
                (rs, rowNum) -> rs.getLong("id")).contains(user.getId())) {
            log.error("пользователь с таким номером уже существует");
            throw new ValidationException("пользователь с таким номером уже существует");
        }
        String sql = "INSERT INTO users VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, currentId, user.getName(), user.getLogin(),
                user.getBirthday(), user.getEmail());
        user.setId(getUniqueID());
        return user;
    }

    @Override
    public User updateUser(User user) {
        ValidateUser.validateUpdateUser(user);
        String sql = "UPDATE users SET name = ?, login = ?, birthday = ?, email = ? WHERE id = ?";
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
        String sql = "SELECT * FROM friendship f " +
                "INNER JOIN users AS u ON f.friend_id = u.id WHERE f.user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        return makeUser(rowSet);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        ValidateUser.validateIdUser(userId);
        ValidateUser.validateIdUser(friendId);
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
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
                "FROM friendship f " +
                "INNER JOIN users u ON f.friend_id = u.id " +
                "WHERE (f.user_id = ? OR f.user_id = ?) AND (f.friend_id != ? AND f.friend_id != ?)";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id, otherId, id, otherId);
        return makeUser(sqlRowSet);
    }

    @Override
    public Optional<User> getUserById(long id) {
        ValidateUser.validateIdUser(id);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
        User user = makeUser(userRows).get(0);
        return Optional.of(user);
    }

    private List<User> makeUser(SqlRowSet rs) {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User(rs.getString("email"), rs.getString("login"),
                    rs.getString("name"), LocalDate.parse(rs.getString(("birthday"))));
            user.setId(rs.getLong("id"));
            users.add(user);
        }
        return users;
    }
}
