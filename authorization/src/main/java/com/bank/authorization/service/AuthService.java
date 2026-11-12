package com.bank.authorization.service;

import com.bank.authorization.dto.JwtResponse;
import com.bank.authorization.dto.LoginRequest;

public interface AuthService {

    JwtResponse login(LoginRequest loginRequest);
}
