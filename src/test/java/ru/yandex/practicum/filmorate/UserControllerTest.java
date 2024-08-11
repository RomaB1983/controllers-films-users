package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController = new UserController();
    User user;

    @BeforeEach
     void init() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.parse("1981-01-01"));
    }

    @Test
    void thrownIsWhitespaceEmail() {
        user.setEmail(" ");
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void thrownIsEmptyEmail() {
        user.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void thrownIsFailEmail() {
        user.setEmail("asdfsad");
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void notThrownIsGoodEmail() {
        user.setEmail("asdfsad@mail.ru");
        assertDoesNotThrow( () -> userController.validateUser(user));
    }

    @Test
    void throwIsEmptyLogin() {
        user.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void throwIsSpaceInLogin() {
        user.setLogin("log in");
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void throwIsBirtdayInFuture() {
        user.setBirthday(LocalDate.parse("2033-01-01"));
        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void notThrowIsBirtdayInPast() {
        user.setBirthday(LocalDate.parse("2019-01-01"));
        assertDoesNotThrow( () -> userController.validateUser(user));
    }
}
