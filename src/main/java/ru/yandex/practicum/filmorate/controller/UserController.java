package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Пользователь {} успешно добавлен", user);
        return user;
    }

    private void checkEmail(User user) {
        log.debug("Проверка на корректность заполнения email: {}", user.getEmail());
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("e-mail указан некорретно: {}", user.getEmail());
            throw new ValidationException("e-mail указан некорретно");
        } else if (isAlreadyExistEmail(user.getEmail())) {
            log.error("email {} уже используется", user.getEmail());
            throw new ValidationException("email " + user.getEmail() + " уже используется");
        }
    }

    private void checkLogin(User user) {
        log.debug("Проверка на корректность заполнения login: {}", user.getLogin());
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и не должен содержать пробелов");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелов");
        } else if (isAlreadyExistLogin(user.getLogin())) {
            log.error("login {} уже используется", user.getLogin());
            throw new ValidationException("login " + user.getLogin() + " уже используется");
        }
    }

    private void checkBirthDay(User user) {
        log.debug("Проверка на корректность заполнения BirhtDay: {}", user.getBirthday());
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения \"{}\" не может быть в будущем", user.getBirthday());
            throw new ValidationException("Дата \"" + user.getBirthday() + "\" рождения не может быть в будущем");
        }
    }

    private void validateUser(User user) {
        log.debug("Проверка на корректность заполнения полей для пользователя: {}", user);
        checkEmail(user);
        checkLogin(user);
        checkBirthDay(user);
    }

    private boolean isAlreadyExistEmail(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    private boolean isAlreadyExistLogin(String login) {
        return users.values().stream().anyMatch(u -> u.getLogin().equals(login));
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("нашли пользователя: {}", oldUser);
            if (newUser.getLogin() != null) {
                checkLogin(newUser);
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getEmail() != null) {
                checkEmail(newUser);
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getBirthday() != null) {
                checkBirthDay(newUser);
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.debug("Пользователь  {} успешно обновлен", oldUser);
            return oldUser;
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}