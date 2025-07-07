package ru.otus.hw.dto;

public record CommentRequest(long bookId,
                             String message) {
}