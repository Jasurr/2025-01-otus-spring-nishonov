package ru.otus.hw.dto;

public record CommentDto(String id,
                         String message,
                         BookDto book) {
}
