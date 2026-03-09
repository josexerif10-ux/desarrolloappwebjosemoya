package com.openwebinars.todo.category.controller;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.category.service.CategoryService;
import com.openwebinars.todo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/submit")
    public String createCategory(@RequestParam String title,
                                 @AuthenticationPrincipal User user) {

        Category category = new Category();
        category.setTitle(title);

        categoryService.saveForUser(category, user);

        return "redirect:/task";
    }
}
