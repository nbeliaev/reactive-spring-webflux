package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ReviewRouterIntgTest {

    private static  final String REVIEW_URL = "/v1/reviews";

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReviewReactiveRepository repository;

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review("abc", 2L, "Excellent Movie", 8.0));
        repository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void addReview() {
        Review review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    Review savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assertThat(savedReview.getId()).isNotNull();
                });

    }

    @Test
    void getAllReviews() {
        webTestClient.get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {
        String id = "abc";
        Review review = new Review("abc", 2L, "Excellent Movie 2", 10.0);

        webTestClient.put()
                .uri(REVIEW_URL + "/{id}", id)
                .bodyValue(review)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    Review updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assertThat(updatedReview.getComment()).isEqualTo("Excellent Movie 2");
                });
    }
}