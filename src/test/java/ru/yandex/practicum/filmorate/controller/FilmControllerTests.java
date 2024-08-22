package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FilmService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_returnFilms_Success() throws Exception {

        // Given
        List<FilmDto> expectedFilms = List.of(
                new FilmDto(1L, "name", "desc", LocalDate.now(), 30, List.of(), new MpaDto(1L, "R1"), List.of()),
                new FilmDto(2L, "name2", "desc2", LocalDate.now(), 40, List.of(), new MpaDto(1L, "R1"), List.of())
        );
        when(service.getFilms()).thenReturn(expectedFilms);

        // When
        MvcResult result = mvc.perform(get("/films")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<FilmDto> actualFilms = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        assertEquals(expectedFilms, actualFilms);
    }
}
