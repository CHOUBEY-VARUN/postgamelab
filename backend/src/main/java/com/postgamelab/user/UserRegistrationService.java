package com.postgamelab.user;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegistrationResponse register(RegisterUserRequest request) {
        String username = request.username().strip();
        String email = request.email().strip().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameAlreadyExistsException();
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                username,
                email,
                passwordHash
        );

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return UserRegistrationResponse.from(savedUser);
        } catch (DataIntegrityViolationException exception) {
            if (userRepository.existsByUsernameIgnoreCase(username)) {
                throw new UsernameAlreadyExistsException();
            }

            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new EmailAlreadyExistsException();
            }

            throw new RegistrationConflictException();
        }
    }
}