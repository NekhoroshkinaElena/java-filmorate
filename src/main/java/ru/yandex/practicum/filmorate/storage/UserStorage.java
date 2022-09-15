package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для хранилища пользователей.
 */
public interface UserStorage {

    int getUniqueID();//ДОБАВИТЬ ДОКУМЕНТАЦИЮ!!!

    /**
     * Получает список всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> getUsers();

    /**
     * Находит пользователя по id.
     *
     * @param id идентификационный номер пользователя
     * @return найденного по id пользователя
     */
    Optional<User> getUserById(long id);

    /**
     * Создаёт и добавляет нового пользователя.
     *
     * @param user пользователь
     * @return созданного пользователя
     */
    User createUser(User user);

    /**
     * Обновляет данные пользователя.
     *
     * @param user пользователь
     * @return данные обновлённого пользователя
     */
    User updateUser(User user);

    void addFriend(long id, long friendId);

    List<User> getListFriends(Long id);

    void deleteFriend(long userId, long friendId);

    List<User> getListCommonFriends(long id, long otherId);
}
