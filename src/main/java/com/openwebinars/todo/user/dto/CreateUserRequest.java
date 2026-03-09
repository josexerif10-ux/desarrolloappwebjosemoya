package com.openwebinars.todo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 30, message = "El username debe tener entre 3 y 30 caracteres")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 60, message = "El nombre completo debe tener entre 3 y 60 caracteres")
    private String fullname;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    // Ejemplo de “fortaleza”: 1 mayúscula, 1 minúscula y 1 número (ajústalo si quieres)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
            message = "La contraseña debe tener mayúscula, minúscula y un número"
    )
    private String password;

    @NotBlank(message = "Debes repetir la contraseña")
    private String verifyPassword;
}
