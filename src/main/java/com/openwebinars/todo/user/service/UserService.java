
package com.openwebinars.todo.user.service;

import com.openwebinars.todo.user.dto.CreateUserRequest;
import com.openwebinars.todo.user.dto.EditProfileRequest;
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

        return userRepository.save(
                User.builder()
                        .username(request.getUsername())
                        .password(encoder.encode(request.getPassword()))
                        .email(request.getEmail())
                        .avatar("/img/AVATARhorrible.png")
                        .fullname(request.getFullname())
                        .role(UserRole.USER)
                        .build()
        );

    }

    public User editProfile(User user, EditProfileRequest request) {
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());

        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        return userRepository.save(user);
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
