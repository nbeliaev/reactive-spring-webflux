package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@ExtendWith(SpringExtension.class)
class ReviewHandlerTest {

    private static  final String REVIEW_URL = "/v1/reviews";

    @MockBean
    private ReviewReactiveRepository repository;
    @Autowired
    private WebTestClient webTestClient;


    @Test
    void addReview() {
        when(repository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        Review review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    Review savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assertThat(savedReview.getId()).isNotNull().isEqualTo("abc");
                });

    }

    @Test
    void addReview_validation() {
        Review review = new Review(null, null, "Awesome Movie", -9.0);

        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isBadRequest();

    }
}