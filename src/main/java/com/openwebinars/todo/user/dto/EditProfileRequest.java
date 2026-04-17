package com.openwebinars.todo.user.dto;

import com.openwebinars.todo.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProfileRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 60, message = "El nombre completo debe tener entre 3 y 60 caracteres")
    private String fullname;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    private String avatar;

    public static EditProfileRequest of(User user) {
        return EditProfileRequest.builder()
                .fullname(user.getFullname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();

    }
}