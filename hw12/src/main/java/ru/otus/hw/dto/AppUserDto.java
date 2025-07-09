package ru.otus.hw.dto;

import java.util.Set;

public record AppUserDto(
        Long id,
        String username,
        Set<RoleDto> roles) {
}
