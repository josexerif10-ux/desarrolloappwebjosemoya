package com.openwebinars.todo.task.controller;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.category.model.CategoryRepository;
import com.openwebinars.todo.category.service.CategoryService;
import com.openwebinars.todo.task.dto.EditTaskRequest;
import com.openwebinars.todo.task.model.Task;
import com.openwebinars.todo.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/")
public class TaskAdminController {

    private final TaskService taskService;
    private final CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.findAll();
    }


    @GetMapping({"/", "/list", "/task"})
    public String adminTaskList(Model model) {

        model.addAttribute("taskList", taskService.findAllAdmin());
        return "admin/admin-tasks";
    }

    @GetMapping(value={"/", "/list", "/task"}, params = "emptyListError")
    public String adminEmptyList(Model model) {
        return "admin/admin-tasks";
    }

    @PostMapping("/task/{id}/del")
    public String adminDeleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/admin/";
    }

    @GetMapping("/task/{id}")
    public String adminViewTask(@PathVariable Long id, Model model) {

        Task task = taskService.findById(id);
        EditTaskRequest editTask = EditTaskRequest.of(task);
        model.addAttribute("task", editTask);
        return "admin/view-task";

    }

}
