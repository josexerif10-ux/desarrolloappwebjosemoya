package com.openwebinars.todo.task.controller;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.category.service.CategoryService;
import com.openwebinars.todo.task.dto.CreateTaskRequest;
import com.openwebinars.todo.task.dto.EditTaskRequest;
import com.openwebinars.todo.task.model.Task;
import com.openwebinars.todo.task.service.TaskService;
import com.openwebinars.todo.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> categories(@AuthenticationPrincipal User user) {
        return categoryService.findAllByUser(user);
    }

    @GetMapping({"/list", "/task"})
    public String taskList(
            Model model,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<Task> taskPage = taskService.findPageFiltered(
                user, title, completed, categoryId, from, to, page, size, sortBy, direction
        );

        model.addAttribute("taskPage", taskPage);
        model.addAttribute("taskList", taskPage.getContent());
        model.addAttribute("newTask", new CreateTaskRequest());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        model.addAttribute("filterTitle", title);
        model.addAttribute("filterCompleted", completed);
        model.addAttribute("filterCategoryId", categoryId);
        model.addAttribute("filterFrom", from);
        model.addAttribute("filterTo", to);

        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "task-list";
    }

    @PostMapping("/task/submit")
    public String taskSubmit(
            @Valid @ModelAttribute("newTask") CreateTaskRequest req,
            BindingResult bindingResult,
            @AuthenticationPrincipal User author,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("taskList", taskService.findAllByUser(author));
            return "task-list";
        }

        taskService.createTask(req, author);

        return "redirect:/";
    }

    @GetMapping("/task/{id}")
    public String viewOrEditTask(@PathVariable Long id,
                                 @AuthenticationPrincipal User user,
                                 Model model) {

        Task task = taskService.findByIdForUser(id, user);
        EditTaskRequest editTask = EditTaskRequest.of(task);
        model.addAttribute("task", editTask);
        return "show-task";
    }

    @PostMapping("/task/edit/submit")
    public String taskEditSubmit(
            @Valid @ModelAttribute("task") EditTaskRequest req,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "show-task";
        }

        taskService.editTaskForUser(req, user);

        return "redirect:/";
    }

    @GetMapping("/task/{id}/toggle")
    public String toggleTask(@PathVariable Long id,
                             @AuthenticationPrincipal User user) {
        taskService.toggleCompleteForUser(id, user);
        return "redirect:/";
    }

    @PostMapping("/task/{id}/del")
    public String deleteTask(@PathVariable Long id,
                             @AuthenticationPrincipal User user) {
        taskService.deleteByIdForUser(id, user);
        return "redirect:/";
    }

    @GetMapping(value = {"/task", "/list"}, params = "emptyListError")
    public String emptyTaskListView(Model model,
                                    @AuthenticationPrincipal User user) {
        model.addAttribute("taskList", List.of());
        model.addAttribute("taskPage", Page.empty());
        model.addAttribute("newTask", new CreateTaskRequest());

        model.addAttribute("currentPage", 0);
        model.addAttribute("pageSize", 5);
        model.addAttribute("totalPages", 0);

        model.addAttribute("filterTitle", null);
        model.addAttribute("filterCompleted", null);
        model.addAttribute("filterCategoryId", null);
        model.addAttribute("filterFrom", null);
        model.addAttribute("filterTo", null);

        model.addAttribute("sortBy", "createdAt");
        model.addAttribute("direction", "asc");

        return "task-list";
    }
}