package ru.otus.hw.services;

import ru.otus.hw.rest.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();
}
