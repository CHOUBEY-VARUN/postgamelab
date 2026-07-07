package com.postgamelab.game;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record GameSessionResponse(
        UUID id,
        String title,
        String homeTeam,
        String awayTeam,
        LocalDate gameDate,
        String videoUrl,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GameSessionResponse from(GameSession gameSession) {
        return new GameSessionResponse(
                gameSession.getId(),
                gameSession.getTitle(),
                gameSession.getHomeTeam(),
                gameSession.getAwayTeam(),
                gameSession.getGameDate(),
                gameSession.getVideoUrl(),
                gameSession.getDescription(),
                gameSession.getCreatedAt(),
                gameSession.getUpdatedAt()
        );
    }
}