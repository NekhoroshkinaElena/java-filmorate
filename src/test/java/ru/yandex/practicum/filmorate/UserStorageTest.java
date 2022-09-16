package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class UserStorageTest {

    private final UserDbStorage userStorage;

    @Test
    public void getUsers(){
        createTestUser("lena@mail.ru", "login", "lena", 1998);
        createTestUser("lena@mail2.ru", "login2", "lena2", 1998);
        assertThat(userStorage.getUsers().size()).isEqualTo(2);
        assertThat(userStorage.getUsers().get(0).getEmail()).isEqualTo("lena@mail.ru");
        assertThat(userStorage.getUsers().get(1).getEmail()).isEqualTo("lena@mail2.ru");
    }

    @Test
    public void createUser(){
        User user = createTestUser("lena@mail.ru", "login", "lenka", 1998);
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getEmail()).isEqualTo("lena@mail.ru");
        assertThat(user.getLogin()).isEqualTo("login");
        assertThat(user.getName()).isEqualTo("lenka");
        assertThat(user.getFriends()).isEqualTo(new HashSet<>());
    }

    @Test
    public void createUserWithExistingId(){
        createTestUser("mail@mail.ru", "login", "name", 1990);
        User user = new User("mail@mail.ru", "login", "name",
                LocalDate.of(1990,7, 1));
        user.setId(1);
        assertThrows(ValidationException.class, () -> userStorage.createUser(user));
    }

    @Test
    public void createUserWithIncorrectId(){
        createTestUser("mail@mail.ru", "login", "name", 1990);
        User user = new User("mail@mail.ru", "login", "name",
                LocalDate.of(1990,7, 1));
        user.setId(-10);
        assertThrows(UserNotFoundException.class, () -> userStorage.createUser(user));
    }

    @Test
    public void createUserWithIncorrectEmail(){
        assertThrows(ValidationException.class, () -> userStorage.createUser(
                createTestUser("mail", "login", "name", 1990)));
    }

    @Test
    public void createUserWithIncorrectLogin(){
        assertThrows(ValidationException.class, () -> userStorage.createUser(
                createTestUser("mail@mail.ru", "login login", "name", 1990)));
    }

    @Test
    public void createUserWithIncorrectBirthday(){
        assertThrows(ValidationException.class, () -> userStorage.createUser(
                createTestUser("mail@mail.ru", "login", "name", 2023)));
    }

    @Test
    public void getUserById(){
        createTestUser("mail@", "login", "name", 1990);
        createTestUser("mail2@", "login2", "name2", 2000);
        assertThat(userStorage.getUserById(2).get().getEmail()).isEqualTo("mail2@");
        assertThat(userStorage.getUserById(2).get().getName()).isEqualTo("name2");
        assertThat(userStorage.getUserById(2).get().getLogin()).isEqualTo("login2");
        assertThat(userStorage.getUserById(2).get().getFriends()).isEqualTo(new HashSet<>());
    }

    @Test
    public void updateUser(){
        createTestUser("mail@", "login", "name", 1990);
        User user = new User("mail2@", "login2", "name2",
                LocalDate.of(2020, 9, 12));
        user.setId(1);
        userStorage.updateUser(user);
        assertThat(userStorage.getUserById(1).get().getEmail()).isEqualTo("mail2@");
    }

    @Test
    public void addFriendAndGetListFriends(){
        User user = createTestUser("mail@", "login", "name", 2022);
        User friend = createTestUser("mail2@", "login2", "name2", 2022);
        User friend2 = createTestUser("mail3@", "login3", "name3", 2021);
        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(user.getId(), friend2.getId());
        assertThat(userStorage.getListFriends(user.getId())).isEqualTo(List.of(friend, friend2));
    }

    @Test
    public void deleteFriend(){
        User user = createTestUser("mail@", "login", "name", 2022);
        User friend = createTestUser("mail2@", "login2", "name2", 2022);
        User friend2 = createTestUser("mail3@", "login3", "name3", 2021);
        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(user.getId(), friend2.getId());
        userStorage.deleteFriend(user.getId(), friend.getId());
        assertThat(userStorage.getListFriends(user.getId())).isEqualTo(List.of(friend2));
    }

    @Test
    public void getListCommonFriends(){
        User user = createTestUser("mail@", "login", "name", 2022);
        User user2 = createTestUser("mail2@", "login2", "name2", 2022);
        User friend = createTestUser("mail3@", "login3", "name3", 2021);
        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(user2.getId(), friend.getId());
        assertThat(userStorage.getListCommonFriends(user.getId(), user2.getId())).isEqualTo(List.of(friend));
    }

    public User createTestUser(String email, String login, String name, int year){
        User user = new User(email, login, name,
                LocalDate.of(year, 9, 10));
        return userStorage.createUser(user);
    }
}
