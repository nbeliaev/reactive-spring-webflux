package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        Flux<MovieInfo> flux = movieInfoRepository.findAll().log();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<MovieInfo> mono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(mono)
                .assertNext(movieInfo -> {
                    assertThat(movieInfo.getName()).isEqualTo("Dark Knight Rises");
                })
                .verifyComplete();
    }

    @Test
    void findById_Empty() {
        Mono<MovieInfo> mono = movieInfoRepository.findById("abc1").log();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mono<MovieInfo> mono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(mono)
                .assertNext(movieInfo1 -> {
                    assertThat(movieInfo1.getId()).isNotNull();
                    assertThat(movieInfo1.getName()).isEqualTo("Batman Begins1");
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        MovieInfo movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2023);

        Mono<MovieInfo> mono = movieInfoRepository.save(movieInfo).log();


        StepVerifier.create(mono)
                .assertNext(movieInfo1 -> {
                    assertThat(movieInfo1.getYear()).isEqualTo(2023);
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        movieInfoRepository.deleteById("abc").block();
        Flux<MovieInfo> flux = movieInfoRepository.findAll().log();

        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();
    }
}