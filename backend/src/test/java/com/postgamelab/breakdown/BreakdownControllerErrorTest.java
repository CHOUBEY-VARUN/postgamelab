package com.postgamelab.breakdown;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.postgamelab.error.GlobalExceptionHandler;

@WebMvcTest(BreakdownController.class)
@Import(GlobalExceptionHandler.class)
class BreakdownControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BreakdownService breakdownService;

    @Test
    void unknownBreakdownIdReturns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(breakdownService.getBreakdownById(id))
                .thenThrow(new BreakdownNotFoundException());

        mockMvc.perform(get("/api/breakdowns/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("BREAKDOWN_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Breakdown not found."))
                .andExpect(jsonPath("$.path").value("/api/breakdowns/" + id))
                .andExpect(jsonPath("$.fieldErrors", empty()));
    }

    @Test
    void unknownBreakdownSlugReturns404() throws Exception {
        String slug = "missing-breakdown";
        when(breakdownService.getBreakdownBySlug(slug))
                .thenThrow(new BreakdownNotFoundException());

        mockMvc.perform(get("/api/breakdowns/public/{slug}", slug))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("BREAKDOWN_NOT_FOUND"))
                .andExpect(jsonPath("$.path")
                        .value("/api/breakdowns/public/" + slug))
                .andExpect(jsonPath("$.fieldErrors", empty()));
    }

    @Test
    void invalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/breakdowns/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_PATH_PARAMETER"))
                .andExpect(jsonPath("$.message").value("Invalid path parameter."))
                .andExpect(jsonPath("$.path").value("/api/breakdowns/not-a-uuid"))
                .andExpect(jsonPath("$.fieldErrors", empty()));
    }

    @Test
    void validationFailureReturns400WithFieldErrors() throws Exception {
        String oversizedAwayTeam = "a".repeat(81);
        String body = """
                {
                  "title": "",
                  "homeTeam": "",
                  "awayTeam": "%s"
                }
                """.formatted(oversizedAwayTeam);

        mockMvc.perform(post("/api/breakdowns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed."))
                .andExpect(jsonPath("$.path").value("/api/breakdowns"))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("awayTeam")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("gameDate")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("homeTeam")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("title")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItem("must not be blank")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItem("must not be null")));
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/breakdowns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"))
                .andExpect(jsonPath("$.message")
                        .value("Request body is missing or malformed."))
                .andExpect(jsonPath("$.fieldErrors", empty()))
                .andExpect(content().string(not(containsString("JsonEOFException"))));
    }

    @Test
    void duplicateSlugReturns409() throws Exception {
        when(breakdownService.createBreakdown(any(CreateBreakdownRequest.class)))
                .thenThrow(new SlugConflictException());

        mockMvc.perform(post("/api/breakdowns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("SLUG_CONFLICT"))
                .andExpect(jsonPath("$.message")
                        .value("A breakdown with this slug already exists."))
                .andExpect(jsonPath("$.path").value("/api/breakdowns"))
                .andExpect(jsonPath("$.fieldErrors", empty()));
    }

    @Test
    void unexpectedFailureReturns500WithoutInternalDetails() throws Exception {
        when(breakdownService.getBreakdownBySlug(anyString()))
                .thenThrow(new RuntimeException(
                        "SQLSTATE 23505 constraint idx_breakdowns_slug password=secret"
                ));

        mockMvc.perform(get("/api/breakdowns/public/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred."))
                .andExpect(jsonPath("$.path")
                        .value("/api/breakdowns/public/internal-error"))
                .andExpect(jsonPath("$.fieldErrors", empty()))
                .andExpect(content().string(not(containsString("SQLSTATE"))))
                .andExpect(content().string(not(containsString("idx_breakdowns_slug"))))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("RuntimeException"))))
                .andExpect(content().string(not(containsString("secret"))));
    }

    private String validRequestBody() {
        return """
                {
                  "title": "Duplicate Slug Test",
                  "homeTeam": "Lakers",
                  "awayTeam": "Warriors",
                  "gameDate": "2026-07-11",
                  "videoUrl": "https://example.com/video",
                  "description": "Test breakdown"
                }
                """;
    }
}
