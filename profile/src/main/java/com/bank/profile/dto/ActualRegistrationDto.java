package com.bank.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ActualRegistrationDto {

    private static final int COUNTRY_MAX_LENGTH = 40;

    private Long id;

    @NotBlank(message = "country is required")
    @Size(max = COUNTRY_MAX_LENGTH, message = "country is too long")
    private String country;

    private String region;
    private String city;
    private String district;
    private String locality;
    private String street;
    private String houseNumber;
    private String houseBlock;
    private String flatNumber;

    @NotNull(message = "index is required")
    private Long index;
}
