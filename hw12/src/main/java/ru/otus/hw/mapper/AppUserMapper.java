package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AppUserDto;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.models.AppUser;

import java.util.stream.Collectors;

@Component
public class AppUserMapper {

    public AppUserDto toDto(AppUser appUser) {
        return new AppUserDto(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getRoles().stream()
                        .map(role -> new RoleDto(role.getId(), role.getName()))
                        .collect(Collectors.toSet())
        );
    }
}
