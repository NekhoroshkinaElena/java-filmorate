package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для хранилища пользователей.
 */
public interface UserStorage {

    /**
     * Генерирует уникальный идентификатор пользователю.
     *
     * @return идентификатор предыдущего пользователя, увеличенный на одну еденицу
     */
    int getUniqueID();

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

    /**
     * Добавляет друга пользователю.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор пользователя для добавления в друзья
     */
    void addFriend(long id, long friendId);

    /**
     * Получает список друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return список друзей пользователя
     */
    List<User> getListFriends(Long id);

    /**
     * Удаляет друга у пользователя.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор пользователя для удаления из друзей
     */
    void deleteFriend(long userId, long friendId);

    /**
     * Получает список общих друзей у двух пользователей.
     *
     * @param id      идентификатор пользователя
     * @param otherId идентификатор другого пользователя
     * @return список общих друзей двух пользователей
     */
    List<User> getListCommonFriends(long id, long otherId);
}
