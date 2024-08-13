package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.helpers.TestData;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ReviewController.class)
public class ReviewControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ReviewService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getReviews_callGetReviewsWithCount10_CountIsNullAndFilmIdIsNull() throws Exception {

        // Given
        List<ReviewDto> expectedReviews = TestData.createReviews();
        when(service.getReviews(10)).thenReturn(expectedReviews);

        // When
        MvcResult result = mvc.perform(get("/reviews")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<ReviewDto> actualReviews = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).getReviews(10);
        Mockito.verifyNoMoreInteractions(service);
        assertEquals(expectedReviews, actualReviews);
    }

    @Test
    void getReviews_callGetReviewsWithFilmId1AndWithCount10_FilmIdIsExistAndCountIsNull() throws Exception {

        // Given
        List<ReviewDto> expectedReviews = TestData.createReviews();
        when(service.getReviews(1L, 10)).thenReturn(expectedReviews);

        // When
        MvcResult result = mvc.perform(get("/reviews?filmId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<ReviewDto> actualReviews = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).getReviews(1L, 10);
        Mockito.verifyNoMoreInteractions(service);
        assertEquals(expectedReviews, actualReviews);
    }

    @Test
    void getReviews_callGetReviewsWithFilmId1AndWithCount5_FilmIdIsExistAndCountIs5() throws Exception {

        // Given
        List<ReviewDto> expectedReviews = TestData.createReviews();
        when(service.getReviews(1L, 5)).thenReturn(expectedReviews);

        // When
        MvcResult result = mvc.perform(get("/reviews?filmId=1&count=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<ReviewDto> actualReviews = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).getReviews(1L, 5);
        Mockito.verifyNoMoreInteractions(service);
        assertEquals(expectedReviews, actualReviews);
    }

}