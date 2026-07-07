package com.postgamelab.game;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;

    public GameSessionService(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    public List<GameSessionResponse> getAllGameSessions() {
        return gameSessionRepository.findAll()
                .stream()
                .map(GameSessionResponse::from)
                .toList();
    }

    public GameSessionResponse getGameSessionById(UUID id) {
        GameSession gameSession = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game session not found"));

        return GameSessionResponse.from(gameSession);
    }

    public GameSessionResponse createGameSession(CreateGameSessionRequest request) {
        GameSession gameSession = new GameSession(
                request.title(),
                request.homeTeam(),
                request.awayTeam(),
                request.gameDate(),
                request.videoUrl(),
                request.description()
        );

        GameSession savedGameSession = gameSessionRepository.save(gameSession);

        return GameSessionResponse.from(savedGameSession);
    }
}