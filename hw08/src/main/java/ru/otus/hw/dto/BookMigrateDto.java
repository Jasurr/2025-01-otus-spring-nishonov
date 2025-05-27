package ru.otus.hw.dto;

import java.util.List;

public record BookMigrateDto(String id,
                             String title,
                             String authorId,
                             List<String> genres) {
}
