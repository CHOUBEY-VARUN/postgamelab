package com.postgamelab.breakdown;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class BreakdownServiceTest {

    @Mock
    private BreakdownRepository breakdownRepository;

    @InjectMocks
    private BreakdownService breakdownService;

    @Test
    void missingBreakdownIdThrowsBreakdownNotFoundException() {
        UUID id = UUID.randomUUID();
        when(breakdownRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                BreakdownNotFoundException.class,
                () -> breakdownService.getBreakdownById(id)
        );
    }

    @Test
    void missingBreakdownSlugThrowsBreakdownNotFoundException() {
        String slug = "missing-breakdown";
        when(breakdownRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThrows(
                BreakdownNotFoundException.class,
                () -> breakdownService.getBreakdownBySlug(slug)
        );
    }

    @Test
    void existingSlugThrowsSlugConflictWithoutSaving() {
        CreateBreakdownRequest request = validRequest();
        when(breakdownRepository.existsBySlug("duplicate-slug-test"))
                .thenReturn(true);

        assertThrows(
                SlugConflictException.class,
                () -> breakdownService.createBreakdown(request)
        );

        verify(breakdownRepository, never()).save(any(Breakdown.class));
    }

    @Test
    void dataIntegrityViolationDuringSaveThrowsSlugConflict() {
        CreateBreakdownRequest request = validRequest();
        when(breakdownRepository.existsBySlug("duplicate-slug-test"))
                .thenReturn(false);
        when(breakdownRepository.save(any(Breakdown.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key violates constraint idx_breakdowns_slug"
                ));

        assertThrows(
                SlugConflictException.class,
                () -> breakdownService.createBreakdown(request)
        );
    }

    private CreateBreakdownRequest validRequest() {
        return new CreateBreakdownRequest(
                "Duplicate Slug Test",
                "Lakers",
                "Warriors",
                LocalDate.of(2026, 7, 11),
                "https://example.com/video",
                "Test breakdown"
        );
    }
}
