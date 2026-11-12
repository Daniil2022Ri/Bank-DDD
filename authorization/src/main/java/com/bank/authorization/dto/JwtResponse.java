package com.bank.authorization.dto;

import com.bank.authorization.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String tokenType;
    private Role role;
    private Long profileId;
    private long expiresInSeconds;
}
