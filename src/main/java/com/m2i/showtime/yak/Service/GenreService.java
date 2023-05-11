package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Repository.GenreRepository;
import org.springframework.stereotype.Service;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
}
