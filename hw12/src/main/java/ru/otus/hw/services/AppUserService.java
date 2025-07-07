package ru.otus.hw.services;

import ru.otus.hw.dto.AppUserDto;

import java.util.List;

public interface AppUserService {

    AppUserDto findByUsername(String username);

    List<AppUserDto> findAll();

}
