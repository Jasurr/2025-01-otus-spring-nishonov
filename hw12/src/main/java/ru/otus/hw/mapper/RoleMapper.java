package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.models.Role;

@Component
public class RoleMapper {

    public RoleDto toDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }

    public Role fromDto(RoleDto roleDto) {
        return new Role(roleDto.id(), roleDto.name());
    }
}
