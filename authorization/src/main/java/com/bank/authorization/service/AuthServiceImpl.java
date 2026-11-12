package com.bank.authorization.service;

import com.bank.authorization.dto.JwtResponse;
import com.bank.authorization.dto.LoginRequest;
import com.bank.authorization.entity.User;
import com.bank.authorization.exception.InvalidCredentialsException;
import com.bank.authorization.repository.UserRepository;
import com.bank.authorization.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String INVALID_CREDENTIALS = "Invalid profileId or password";
    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest loginRequest) {
        final User user = userRepository.findByProfileId(loginRequest.getProfileId())
                .filter(foundUser -> passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));
        return JwtResponse.builder()
                .token(jwtTokenUtils.generateToken(user))
                .tokenType(TOKEN_TYPE)
                .role(user.getRole())
                .profileId(user.getProfileId())
                .expiresInSeconds(jwtTokenUtils.getLifetimeSeconds())
                .build();
    }
}
