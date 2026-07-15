package com.postgamelab.user;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserLoginService userLoginService;

    @BeforeEach
    void setUp() {
        userLoginService = new UserLoginService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void successfulLoginNormalizesEmailAndReturnsTokenAndSafeUser() {
        UUID id = UUID.randomUUID();
        User user = mock(User.class);

        when(user.getId()).thenReturn(id);
        when(user.getUsername()).thenReturn("varun");
        when(user.getEmail()).thenReturn("varun@example.com");
        when(user.getPasswordHash()).thenReturn("stored-password-hash");

        when(userRepository.findByEmailIgnoreCase("varun@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "password123",
                "stored-password-hash"
        )).thenReturn(true);
        when(jwtService.generateToken(user))
                .thenReturn("signed.jwt.token");

        LoginUserResponse response = userLoginService.login(
                new LoginUserRequest(
                        "  VARUN@EXAMPLE.COM  ",
                        "password123"
                )
        );

        assertEquals("signed.jwt.token", response.token());
        assertEquals(id, response.user().id());
        assertEquals("varun", response.user().username());
        assertEquals("varun@example.com", response.user().email());

        verify(passwordEncoder).matches(
                "password123",
                "stored-password-hash"
        );
        verify(jwtService).generateToken(user);
    }

    @Test
    void unknownEmailThrowsGenericInvalidCredentialsError() {
        when(userRepository.findByEmailIgnoreCase("missing@example.com"))
                .thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userLoginService.login(
                        new LoginUserRequest(
                                "MISSING@EXAMPLE.COM",
                                "password123"
                        )
                )
        );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );
        verify(jwtService, never()).generateToken(
                org.mockito.ArgumentMatchers.any(User.class)
        );
    }

    @Test
    void incorrectPasswordThrowsSameGenericInvalidCredentialsError() {
        User user = mock(User.class);

        when(user.getPasswordHash()).thenReturn("stored-password-hash");
        when(userRepository.findByEmailIgnoreCase("varun@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "incorrect123",
                "stored-password-hash"
        )).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userLoginService.login(
                        new LoginUserRequest(
                                "varun@example.com",
                                "incorrect123"
                        )
                )
        );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );
        verify(jwtService, never()).generateToken(user);
    }
}