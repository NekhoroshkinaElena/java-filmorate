package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс для хранилища пользователей
 */
public interface UserStorage {

    /**
     * получает список всех пользователей
     *
     * @return список всех пользователей
     */
    List<User> getUsers();

    /**
     * находит пользователя по id
     *
     * @param id идентификационный номер пользователя
     * @return найденного по id пользователя
     */
    User getUser(long id);

    /**
     * создаёт и добавляет нового пользователя
     *
     * @param user пользователь
     * @return созданного пользователя
     */
    User createUser(User user);

    /**
     * обновляет данные пользователя
     *
     * @param user пользователь
     * @return данные обновлённого пользователя
     */
    User updateUser(User user);
}
