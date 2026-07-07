package com.postgamelab.breakdown;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "breakdowns")
public class Breakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 180)
    private String slug;

    @Column(name = "home_team", nullable = false, length = 80)
    private String homeTeam;

    @Column(name = "away_team", nullable = false, length = 80)
    private String awayTeam;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BreakdownVisibility visibility;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Breakdown() {
    }

    public Breakdown(
            String title,
            String slug,
            String homeTeam,
            String awayTeam,
            LocalDate gameDate,
            String videoUrl,
            String description,
            BreakdownVisibility visibility
    ) {
        this.title = title;
        this.slug = slug;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameDate = gameDate;
        this.videoUrl = videoUrl;
        this.description = description;
        this.visibility = visibility;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public BreakdownVisibility getVisibility() {
        return visibility;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
