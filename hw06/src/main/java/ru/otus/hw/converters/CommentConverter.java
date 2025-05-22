package ru.otus.hw.converters;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    public String commentToString(long id, String message) {
        return "Id: %d, message: %s".formatted(id, message);
    }

    public String commentToString(long id, String message, long bookId) {
        return "Id: %d, message: %s, bookId: %d".formatted(id, message, bookId);
    }
}
