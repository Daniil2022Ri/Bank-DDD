package com.bank.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private Long id;

    @NotNull(message = "phoneNumber is required")
    @Positive(message = "phoneNumber must be positive")
    private Long phoneNumber;

    @Email(message = "email is invalid")
    private String email;

    private String nameOnCard;

    @Positive(message = "inn must be positive")
    private Long inn;

    @Positive(message = "snils must be positive")
    private Long snils;

    @NotNull(message = "passportId is required")
    @Positive(message = "passportId must be positive")
    private Long passportId;

    @Positive(message = "actualRegistrationId must be positive")
    private Long actualRegistrationId;
}
