package com.openwebinars.todo.user.controller;

import com.openwebinars.todo.user.dto.CreateUserRequest;
import com.openwebinars.todo.user.model.User;
import com.openwebinars.todo.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/auth/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "register";
    }

    @PostMapping("/auth/register/submit")
    public String processRegisterForm(
            @Valid @ModelAttribute("user") CreateUserRequest request,
            BindingResult bindingResult) {

        if (!request.getPassword().equals(request.getVerifyPassword())) {
            bindingResult.rejectValue("verifyPassword", "password.mismatch", "Las contraseñas no coinciden");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerUser(request);

        return "redirect:/login";
    }
}