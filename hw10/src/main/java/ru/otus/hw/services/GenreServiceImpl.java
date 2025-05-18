package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.rest.dto.GenreDto;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();
    }
}
