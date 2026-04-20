package com.openwebinars.todo.user.service;

import com.openwebinars.todo.user.dto.CreateUserRequest;
import com.openwebinars.todo.user.dto.EditProfileRequest;
import com.openwebinars.todo.user.exception.EmailAlreadyExistsException;
import com.openwebinars.todo.user.exception.UsernameAlreadyExistsException;
import com.openwebinars.todo.user.model.User;
import com.openwebinars.todo.user.model.UserRepository;
import com.openwebinars.todo.user.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User registerUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        return userRepository.save(
                User.builder()
                        .username(request.getUsername())
                        .password(encoder.encode(request.getPassword()))
                        .email(request.getEmail())
                        .fullname(request.getFullname())
                        .role(UserRole.USER)
                        .avatar("/img/AVATARhorrible.png")
                        .build()
        );
    }

    public User editProfile(User user, EditProfileRequest request) {

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
            throw new EmailAlreadyExistsException("El email ya está en uso");
        }

        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());

        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        return userRepository.save(user);
    }

    public long countAllUsers() {
        return userRepository.count();
    }

    public User changeRole(User user, UserRole userRole) {
        user.setRole(userRole);
        return userRepository.save(user);
    }

    public User changeRole(Long userId, UserRole userRole) {
        return userRepository.findById(userId)
                .map(u -> {
                    u.setRole(userRole);
                    return userRepository.save(u);
                }).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll(Sort.by("username"));
    }
}