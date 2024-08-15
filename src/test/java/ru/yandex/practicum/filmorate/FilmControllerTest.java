package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private static final LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);
    private final static Integer MAX_LENGTH_DESCRIPTION = 200;

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
        assertEquals(assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage(),"Наименование фильма не может быть пустым");
    }

    @Test
    void thrownIsEmptyName() {
        film.setName(null);
        assertEquals(assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage(),"Наименование фильма не может быть пустым");
    }

    @Test
    void thrownIsBigDescription() {
        film.setDescription("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\n" +
                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        assertEquals(assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage(),"Описание фильма не может быть больше " + MAX_LENGTH_DESCRIPTION + " символов");
    }

    @Test
    void throwIsReleaseDateSlow() {
        film.setReleaseDate(MIN_DATE_RELEASE.minusDays(1));
        assertEquals(assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage(),"Дата фильма \"" + film.getReleaseDate() + "\" не может быть ранее " + MIN_DATE_RELEASE);
    }
    @Test
    void throwIsNegativeDuration() {
        film.setDuration(-1);
        assertEquals(assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage(),"Продолжительность фильма не может быть < 0");
    }
}
