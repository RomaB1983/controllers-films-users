package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.controller.FilmController.MIN_DATE_RELEASE;

public class FilmControllerTest {
    FilmController filmController = new FilmController();
    Film film;

    @BeforeEach
    void init() {
        film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.parse("1981-01-01"));
    }

    @Test
    void thrownIsWhitespaceFilm() {
        film.setName(" ");
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void thrownIsEmptyName() {
        film.setName(null);
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void thrownIsBigDescription() {
        film.setDescription("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\n" +
                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void throwIsReleaseDateSlow() {
        film.setReleaseDate(MIN_DATE_RELEASE.minusDays(1));
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }
    @Test
    void throwIsNegativeDuration() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }
}
