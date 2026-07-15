package com.postgamelab.user;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.postgamelab.error.GlobalExceptionHandler;

@WebMvcTest(UserLoginController.class)
@Import(GlobalExceptionHandler.class)
class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserLoginService userLoginService;

    @Test
    void successfulLoginReturnsTokenAndSafeUserWithoutPasswordData()
            throws Exception {
        UUID id = UUID.randomUUID();

        when(userLoginService.login(any(LoginUserRequest.class)))
                .thenReturn(new LoginUserResponse(
                        "signed.jwt.token",
                        new AuthenticatedUserResponse(
                                id,
                                "varun",
                                "varun@example.com"
                        )
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.token")
                        .value("signed.jwt.token"))
                .andExpect(jsonPath("$.user.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.user.username")
                        .value("varun"))
                .andExpect(jsonPath("$.user.email")
                        .value("varun@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andExpect(content().string(
                        not(containsString("password123"))
                ))
                .andExpect(content().string(
                        not(containsString("stored-password-hash"))
                ));
    }

    @Test
    void unknownEmailReturnsGeneric401Response() throws Exception {
        when(userLoginService.login(any(LoginUserRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "missing@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code")
                        .value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password."))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/login"))
                .andExpect(jsonPath("$.fieldErrors", empty()));
    }

    @Test
    void incorrectPasswordReturnsSameGeneric401Response()
            throws Exception {
        when(userLoginService.login(any(LoginUserRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "varun@example.com",
                                  "password": "incorrect123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code")
                        .value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password."))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/login"))
                .andExpect(jsonPath("$.fieldErrors", empty()));
    }

    @Test
    void invalidLoginRequestReturnsValidationFieldErrors()
            throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed."))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/login"))
                .andExpect(jsonPath("$.fieldErrors", not(empty())))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        hasItem("email")))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        hasItem("password")));
    }

    private String validRequestBody() {
        return """
                {
                  "email": "varun@example.com",
                  "password": "password123"
                }
                """;
    }
}