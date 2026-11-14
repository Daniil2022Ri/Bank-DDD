package com.bank.authorization.mapper;

import com.bank.authorization.dto.UserCreateDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserCreateDto createDto);
}
