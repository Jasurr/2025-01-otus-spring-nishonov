package ru.otus.hw.services;

import ru.otus.hw.dto.RoleDto;

import java.util.List;

public interface RoleService {

    List<RoleDto> findAll();

    RoleDto findByName(String name);
}
