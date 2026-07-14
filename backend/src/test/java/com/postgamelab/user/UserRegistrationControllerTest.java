package com.postgamelab.user;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.postgamelab.error.GlobalExceptionHandler;

@WebMvcTest(UserRegistrationController.class)
@Import(GlobalExceptionHandler.class)
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @Test
    void successfulRegistrationReturns201WithoutPasswordData() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 14, 12, 30);

        when(userRegistrationService.register(any(RegisterUserRequest.class)))
                .thenReturn(new UserRegistrationResponse(
                        id,
                        "varun",
                        "varun@example.com",
                        createdAt
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.username").value("varun"))
                .andExpect(jsonPath("$.email").value("varun@example.com"))
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(content().string(not(containsString("password123"))));
    }

    @Test
    void duplicateUsernameReturns409WithUsernameFieldError() throws Exception {
        when(userRegistrationService.register(any(RegisterUserRequest.class)))
                .thenThrow(new UsernameAlreadyExistsException());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code")
                        .value("USERNAME_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message")
                        .value("This username is already registered."))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value("username"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("This username is already registered."));
    }

    @Test
    void duplicateEmailReturns409WithEmailFieldError() throws Exception {
        when(userRegistrationService.register(any(RegisterUserRequest.class)))
                .thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code")
                        .value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message")
                        .value("This email address is already registered."))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value("email"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("This email address is already registered."));
    }

    @Test
    void invalidRegistrationReturnsValidationFieldErrors() throws Exception {
        String body = """
                {
                  "username": "bad name!",
                  "email": "not-an-email",
                  "password": "short"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed."))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors", not(empty())))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        hasItem("username")))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        hasItem("email")))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        hasItem("password")));
    }

    private String validRequestBody() {
        return """
                {
                  "username": "varun",
                  "email": "varun@example.com",
                  "password": "password123"
                }
                """;
    }
}
