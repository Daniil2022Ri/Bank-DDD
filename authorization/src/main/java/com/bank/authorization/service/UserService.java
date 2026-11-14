package com.bank.authorization.service;

import com.bank.authorization.dto.UserCreateDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.dto.UserUpdateDto;
import java.util.List;

public interface UserService {

    UserDto create(UserCreateDto createDto);

    UserDto getById(Long userId);

    List<UserDto> getAll();

    UserDto update(Long userId, UserUpdateDto updateDto);

    void delete(Long userId);
}
