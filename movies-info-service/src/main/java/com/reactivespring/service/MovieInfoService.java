package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieInfoService {

    private final MovieInfoRepository repository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return repository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return repository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return repository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {
        return repository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setReleaseDate(updatedMovieInfo.getReleaseDate());
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    return repository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return repository.deleteById(id);
    }

    public Flux<MovieInfo> getMoviesInfoByYear(Integer year) {
        return repository.findByYear(year);
    }
}
