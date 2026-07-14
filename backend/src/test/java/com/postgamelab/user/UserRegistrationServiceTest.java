package com.postgamelab.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userRegistrationService = new UserRegistrationService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void successfulRegistrationNormalizesFieldsAndStoresHashedPassword() {
        RegisterUserRequest request = new RegisterUserRequest(
                "  Varun_23  ",
                "  VARUN@EXAMPLE.COM  ",
                "password123"
        );

        AtomicReference<User> savedUserReference = new AtomicReference<>();

        when(userRepository.existsByUsernameIgnoreCase("Varun_23"))
                .thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("varun@example.com"))
                .thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    savedUserReference.set(user);
                    return user;
                });

        UserRegistrationResponse response =
                userRegistrationService.register(request);

        User savedUser = savedUserReference.get();

        assertEquals("Varun_23", savedUser.getUsername());
        assertEquals("varun@example.com", savedUser.getEmail());
        assertFalse(savedUser.getPasswordHash().equals("password123"));
        assertTrue(passwordEncoder.matches(
                "password123",
                savedUser.getPasswordHash()
        ));

        assertEquals("Varun_23", response.username());
        assertEquals("varun@example.com", response.email());
    }

    @Test
    void duplicateUsernameIsRejectedWithoutSaving() {
        RegisterUserRequest request = validRequest();

        when(userRepository.existsByUsernameIgnoreCase("varun"))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userRegistrationService.register(request)
        );

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void duplicateEmailIsRejectedWithoutSaving() {
        RegisterUserRequest request = validRequest();

        when(userRepository.existsByUsernameIgnoreCase("varun"))
                .thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("varun@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userRegistrationService.register(request)
        );

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    private RegisterUserRequest validRequest() {
        return new RegisterUserRequest(
                "varun",
                "varun@example.com",
                "password123"
        );
    }
}
