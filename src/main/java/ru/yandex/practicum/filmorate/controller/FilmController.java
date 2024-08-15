package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final static LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);
    private final static Integer MAX_LENGTH_DESCRIPTION = 200;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.debug("Фильм {} успешно добавлен", film);
        return film;
    }

    private void checkName(Film film) {
        log.debug("Проверка на корректность заполнения наименования: {}", film.getName());
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Наименование фильма не может быть пустым");
            throw new ValidationException("Наименование фильма не может быть пустым");
        }
    }

    private void checkDescription(Film film) {
        log.debug("Проверка на корректность заполнения описание: {}", film.getDescription());
        if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            log.error("Описание фильма не может быть больше {} символов", MAX_LENGTH_DESCRIPTION);
            throw new ValidationException("Описание фильма не может быть больше " + MAX_LENGTH_DESCRIPTION + " символов");
        }
    }

    private void checkReleaseDate(Film film) {
        log.debug("Проверка на корректность заполнения описания: {}", film.getDescription());
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.error("Дата фильма \"{}\" не может быть ранее {}", film.getReleaseDate(), MIN_DATE_RELEASE);
            throw new ValidationException("Дата фильма \"" + film.getReleaseDate() + "\" не может быть ранее " + MIN_DATE_RELEASE);
        }
    }

    private void checkDuration(Film film) {
        log.debug("Проверка на корректность заполнения длительности: {}", film.getDuration());
        if (film.getDuration() != null && film.getDuration() < 0) {
            log.error("Продолжительность фильма не может быть < 0");
            throw new ValidationException("Продолжительность фильма не может быть < 0");
        }
    }

    private void validateFilm(Film film) {
        log.debug("Проверка на корректность заполнения полей для фильма: {}", film);
        checkName(film);
        checkDescription(film);
        checkReleaseDate(film);
        checkDuration(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Id фильма должен быть указан");
            throw new ValidationException("Id фильма должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            log.debug("нашли фильм: " + newFilm);
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                checkName(newFilm);
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null) {
                checkDuration(newFilm);
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null) {
                checkReleaseDate(newFilm);
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            log.debug("Фильм {} успешно обновлен", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
