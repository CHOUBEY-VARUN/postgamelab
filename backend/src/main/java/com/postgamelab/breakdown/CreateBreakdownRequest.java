package com.postgamelab.breakdown;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBreakdownRequest(
        @NotBlank
        @Size(max = 160)
        String title,

        @NotBlank
        @Size(max = 80)
        String homeTeam,

        @NotBlank
        @Size(max = 80)
        String awayTeam,

        @NotNull
        LocalDate gameDate,

        String videoUrl,

        String description
) {
}
