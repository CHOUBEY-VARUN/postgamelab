package com.postgamelab.breakdown;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
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
                .orElseThrow(BreakdownNotFoundException::new);

        return BreakdownResponse.from(breakdown);
    }

    public BreakdownResponse getBreakdownBySlug(String slug) {
        Breakdown breakdown = breakdownRepository.findBySlug(slug)
                .orElseThrow(BreakdownNotFoundException::new);

        return BreakdownResponse.from(breakdown);
    }

    public BreakdownResponse createBreakdown(CreateBreakdownRequest request) {
        String slug = generateSlug(request.title());

        if (breakdownRepository.existsBySlug(slug)) {
            throw new SlugConflictException();
        }

        Breakdown breakdown = new Breakdown(
                request.title(),
                slug,
                request.homeTeam(),
                request.awayTeam(),
                request.gameDate(),
                request.videoUrl(),
                request.description(),
                BreakdownVisibility.PRIVATE
        );

        try {
            Breakdown savedBreakdown = breakdownRepository.save(breakdown);
            return BreakdownResponse.from(savedBreakdown);
        } catch (DataIntegrityViolationException exception) {
            throw new SlugConflictException();
        }
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
