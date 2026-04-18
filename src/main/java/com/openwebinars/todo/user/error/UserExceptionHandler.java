package com.openwebinars.todo.user.error;

import com.openwebinars.todo.user.dto.CreateUserRequest;
import com.openwebinars.todo.user.dto.EditProfileRequest;
import com.openwebinars.todo.user.exception.EmailAlreadyExistsException;
import com.openwebinars.todo.user.exception.UsernameAlreadyExistsException;
import com.openwebinars.todo.user.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handleUsernameAlreadyExists(
            UsernameAlreadyExistsException ex,
            Model model) {

        CreateUserRequest form = (CreateUserRequest) model.getAttribute("user");
        if (form == null) {
            form = new CreateUserRequest();
        }

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "user");
        bindingResult.rejectValue("username", "username.exists", ex.getMessage());

        model.addAttribute("user", form);
        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "user", bindingResult);

        return "register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            Model model,
            @AuthenticationPrincipal User currentUser) {

        EditProfileRequest profileForm = (EditProfileRequest) model.getAttribute("profileForm");

        if (profileForm != null) {
            BindingResult bindingResult = new BeanPropertyBindingResult(profileForm, "profileForm");
            bindingResult.rejectValue("email", "email.exists", ex.getMessage());

            model.addAttribute("profileForm", profileForm);
            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "profileForm", bindingResult);

            return "edit-profile";
        }

        CreateUserRequest registerForm = (CreateUserRequest) model.getAttribute("user");
        if (registerForm == null) {
            registerForm = new CreateUserRequest();
        }

        BindingResult bindingResult = new BeanPropertyBindingResult(registerForm, "user");
        bindingResult.rejectValue("email", "email.exists", ex.getMessage());

        model.addAttribute("user", registerForm);
        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "user", bindingResult);

        return "register";
    }
}