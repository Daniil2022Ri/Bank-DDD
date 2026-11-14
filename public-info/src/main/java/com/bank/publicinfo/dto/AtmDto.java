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
public class AtmDto {

    private Long id;

    @NotBlank(message = "address is required")
    private String address;

    private OffsetTime startOfWork;

    private OffsetTime endOfWork;

    @NotNull(message = "allHours is required")
    private Boolean allHours;

    @Positive(message = "branchId must be positive")
    private Long branchId;
}
