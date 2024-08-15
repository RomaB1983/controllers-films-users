package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController = new UserController();
    User user;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1L);
        user.setName("name");
        user.setLogin("login");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.parse("1981-01-01"));
    }

    @Test
    void thrownIsWhitespaceEmail() {
        user.setEmail(" ");
        assertEquals(assertThrows(ValidationException.class, () -> userController.create(user)).getMessage(), "e-mail указан некорретно");
    }

    @Test
    void thrownIsEmptyEmail() {
        user.setEmail(null);
        assertEquals(assertThrows(ValidationException.class, () -> userController.create(user)).getMessage(), "e-mail указан некорретно");
    }

    @Test
    void thrownIsFailEmail() {
        user.setEmail("asdfsad");
        assertEquals(assertThrows(ValidationException.class, () -> userController.create(user)).getMessage(), "e-mail указан некорретно");
    }

    @Test
    void notThrownIsGoodEmail() {
        user.setEmail("asdfsad@mail.ru");
        assertDoesNotThrow(() -> userController.create(user));
    }

    @Test
    void throwIsEmptyLogin() {
        user.setLogin(null);
        assertEquals(assertThrows(ValidationException.class, () -> userController.create(user)).getMessage(), "Логин не может быть пустым и не должен содержать пробелов");
    }

    @Test
    void throwIsSpaceInLogin() {
        user.setLogin("log in");
        assertEquals(assertThrows(ValidationException.class, () -> userController.create(user)).getMessage(), "Логин не может быть пустым и не должен содержать пробелов");
    }

    @Test
    void throwIsBirtdayInFuture() {
        user.setBirthday(LocalDate.parse("2033-01-01"));
        assertEquals(assertThrows(ValidationException.class, () -> userController.create(user)).getMessage(), "Дата \"2033-01-01\" рождения не может быть в будущем");
    }

    @Test
    void notThrowIsBirtdayInPast() {
        user.setBirthday(LocalDate.parse("2019-01-01"));
        assertDoesNotThrow(() -> userController.create(user));
    }
}
