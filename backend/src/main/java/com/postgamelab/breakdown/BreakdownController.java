package com.postgamelab.breakdown;

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
@RequestMapping("/api/breakdowns")
public class BreakdownController {

    private final BreakdownService breakdownService;

    public BreakdownController(BreakdownService breakdownService) {
        this.breakdownService = breakdownService;
    }

    @GetMapping
    public List<BreakdownResponse> getAllBreakdowns() {
        return breakdownService.getAllBreakdowns();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BreakdownResponse createBreakdown(
            @Valid @RequestBody CreateBreakdownRequest request
    ) {
        return breakdownService.createBreakdown(request);
    }

    @GetMapping("/{id}")
    public BreakdownResponse getBreakdownById(@PathVariable UUID id) {
        return breakdownService.getBreakdownById(id);
    }

    @GetMapping("/public/{slug}")
    public BreakdownResponse getBreakdownBySlug(@PathVariable String slug) {
        return breakdownService.getBreakdownBySlug(slug);
    }
}
