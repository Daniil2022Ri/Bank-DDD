package com.bank.publicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetTime;
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
public class BranchDto {

    private Long id;

    @NotBlank(message = "address is required")
    private String address;

    @NotNull(message = "phoneNumber is required")
    @Positive(message = "phoneNumber must be positive")
    private Long phoneNumber;

    @NotBlank(message = "city is required")
    private String city;

    @NotNull(message = "startOfWork is required")
    private OffsetTime startOfWork;

    @NotNull(message = "endOfWork is required")
    private OffsetTime endOfWork;
}
