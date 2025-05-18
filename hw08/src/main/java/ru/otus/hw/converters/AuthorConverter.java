package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDTO;

@Component
public class AuthorConverter {
    public String authorToString(AuthorDTO author) {
        return "Id: %s, FullName: %s".formatted(author.getId(), author.getFullName());
    }
}
