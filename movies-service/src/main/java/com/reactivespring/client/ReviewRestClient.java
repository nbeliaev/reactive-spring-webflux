package com.reactivespring.client;

import com.reactivespring.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ReviewRestClient {

    private final WebClient webClient;
    @Value("${restclient.reviewUrl}")
    private String reviewUrl;

    public Flux<Review> retrieveReviews(String movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(reviewUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Review.class);
    }
}