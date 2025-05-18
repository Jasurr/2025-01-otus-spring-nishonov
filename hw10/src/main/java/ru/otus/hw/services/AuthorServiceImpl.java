package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.rest.dto.AuthorDto;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(author -> new AuthorDto(author.getId(), author.getFullName()))
                .toList();
    }
}
