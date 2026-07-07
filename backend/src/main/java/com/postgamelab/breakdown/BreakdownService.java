package com.postgamelab.breakdown;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BreakdownService {

    private final BreakdownRepository breakdownRepository;

    public BreakdownService(BreakdownRepository breakdownRepository) {
        this.breakdownRepository = breakdownRepository;
    }

    public List<BreakdownResponse> getAllBreakdowns() {
        return breakdownRepository.findAll()
                .stream()
                .map(BreakdownResponse::from)
                .toList();
    }

    public BreakdownResponse getBreakdownById(UUID id) {
        Breakdown breakdown = breakdownRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breakdown not found"));

        return BreakdownResponse.from(breakdown);
    }

    public BreakdownResponse getBreakdownBySlug(String slug) {
        Breakdown breakdown = breakdownRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Breakdown not found"));

        return BreakdownResponse.from(breakdown);
    }

    public BreakdownResponse createBreakdown(CreateBreakdownRequest request) {
        Breakdown breakdown = new Breakdown(
                request.title(),
                generateSlug(request.title()),
                request.homeTeam(),
                request.awayTeam(),
                request.gameDate(),
                request.videoUrl(),
                request.description(),
                BreakdownVisibility.PRIVATE
        );

        Breakdown savedBreakdown = breakdownRepository.save(breakdown);

        return BreakdownResponse.from(savedBreakdown);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
