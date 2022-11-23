package com.m2i.showtime.yak;

import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes= Application.class)
@RunWith(SpringJUnit4ClassRunner.class)

public class MovieTest {
    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;


    @Test
    public void testGetMovieOrCreateIfNotExist() {
        Movie movie = new Movie(45L, "test");
        Movie savedMovie = movieService.getMovieOrCreateIfNotExist(Long.valueOf(45), "test");
        assertEquals("test",savedMovie.getName());
    }
}
