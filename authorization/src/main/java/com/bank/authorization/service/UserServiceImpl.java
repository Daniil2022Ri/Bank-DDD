package com.bank.authorization.service;

import com.bank.authorization.dto.UserCreateDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.dto.UserUpdateDto;
import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.exception.DuplicateUserException;
import com.bank.authorization.exception.EntityNotFoundException;
import com.bank.authorization.mapper.UserMapper;
import com.bank.authorization.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User with id %d not found";
    private static final String PROFILE_ALREADY_EXISTS = "User with profileId %d already exists";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto create(UserCreateDto createDto) {
        if (userRepository.existsByProfileId(createDto.getProfileId())) {
            throw new DuplicateUserException(String.format(PROFILE_ALREADY_EXISTS, createDto.getProfileId()));
        }
        final User user = userMapper.toEntity(createDto);
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setRole(Role.ROLE_USER);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        return userMapper.toDto(findUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserDto update(Long userId, UserUpdateDto updateDto) {
        final User user = findUser(userId);
        final String newPassword = updateDto.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        if (updateDto.getRole() != null) {
            user.setRole(updateDto.getRole());
        }
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(findUser(userId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND, userId)));
    }
}
