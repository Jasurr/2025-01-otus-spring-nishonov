package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;

public interface CommentRepositoryCustom {
    void updateCommentsByBookId(String bookId, Book updatedBook);
}
