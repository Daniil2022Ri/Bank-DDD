package com.bank.profile.dto;

import com.bank.profile.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
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
public class PassportDto {

    private Long id;

    @NotNull(message = "series is required")
    @Positive(message = "series must be positive")
    private Integer series;

    @NotNull(message = "number is required")
    @Positive(message = "number must be positive")
    private Long number;

    @NotBlank(message = "lastName is required")
    private String lastName;

    @NotBlank(message = "firstName is required")
    private String firstName;

    private String middleName;

    @NotNull(message = "gender is required")
    private Gender gender;

    @NotNull(message = "birthDate is required")
    @Past(message = "birthDate must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "birthPlace is required")
    private String birthPlace;

    @NotBlank(message = "issuedBy is required")
    private String issuedBy;

    @NotNull(message = "dateOfIssue is required")
    @PastOrPresent(message = "dateOfIssue must not be in the future")
    private LocalDate dateOfIssue;

    @NotNull(message = "divisionCode is required")
    @Positive(message = "divisionCode must be positive")
    private Integer divisionCode;

    private LocalDate expirationDate;

    @NotNull(message = "registrationId is required")
    @Positive(message = "registrationId must be positive")
    private Long registrationId;
}
