package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewHandler {

    private final ReviewReactiveRepository repository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(repository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);

    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        Optional<String> optnMovieInfoId = request.queryParam("movieInfoId");
        Flux<Review> flux;
        if (optnMovieInfoId.isPresent()) {
            flux = repository.findAllByMovieInfoId(Long.valueOf(optnMovieInfoId.get()));
        } else {
            flux = repository.findAll();
        }
        return ServerResponse.ok().body(flux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String id = request.pathVariable("id");
        return repository.findById(id)
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        }))
                .flatMap(repository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview));
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        String id = request.pathVariable("id");
        return repository.findById(id)
                .flatMap(repository::delete)
                .then(ServerResponse.noContent().build());
    }
}
