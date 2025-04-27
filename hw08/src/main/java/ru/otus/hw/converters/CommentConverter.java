package ru.otus.hw.converters;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    public String commentToString(String id, String message) {
        return "Id: %s, message: %s".formatted(id, message);
    }

    public String commentToString(String id, String message, String bookId) {
        return "Id: %s, message: %s, bookId: %s".formatted(id, message, bookId);
    }
}
