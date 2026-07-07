package com.postgamelab.game;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/game-sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @GetMapping
    public List<GameSessionResponse> getAllGameSessions() {
        return gameSessionService.getAllGameSessions();
    }

    @GetMapping("/{id}")
    public GameSessionResponse getGameSessionById(@PathVariable UUID id) {
        return gameSessionService.getGameSessionById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameSessionResponse createGameSession(
            @Valid @RequestBody CreateGameSessionRequest request
    ) {
        return gameSessionService.createGameSession(request);
    }
}