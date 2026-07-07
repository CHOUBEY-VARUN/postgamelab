package com.postgamelab.breakdown;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BreakdownResponse(
        UUID id,
        String title,
        String slug,
        String homeTeam,
        String awayTeam,
        LocalDate gameDate,
        String videoUrl,
        String description,
        BreakdownVisibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BreakdownResponse from(Breakdown breakdown) {
        return new BreakdownResponse(
                breakdown.getId(),
                breakdown.getTitle(),
                breakdown.getSlug(),
                breakdown.getHomeTeam(),
                breakdown.getAwayTeam(),
                breakdown.getGameDate(),
                breakdown.getVideoUrl(),
                breakdown.getDescription(),
                breakdown.getVisibility(),
                breakdown.getCreatedAt(),
                breakdown.getUpdatedAt()
        );
    }
}
