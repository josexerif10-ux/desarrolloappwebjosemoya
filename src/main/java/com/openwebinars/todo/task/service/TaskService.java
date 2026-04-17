package com.openwebinars.todo.task.service;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.category.model.CategoryRepository;
import com.openwebinars.todo.tag.service.TagService;
import com.openwebinars.todo.task.dto.CreateTaskRequest;
import com.openwebinars.todo.task.dto.EditTaskRequest;
import com.openwebinars.todo.task.exception.EmptyTaskListException;
import com.openwebinars.todo.task.exception.TaskNotFoundException;
import com.openwebinars.todo.task.model.Task;
import com.openwebinars.todo.task.model.TaskRepository;
import com.openwebinars.todo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;

    private List<Task> findAll(User user) {
        if (user != null) {
            return taskRepository.findByAuthor(user, Sort.by("createdAt").ascending());
        } else {
            return taskRepository.findAll(Sort.by("createdAt").ascending());
        }
    }

    public List<Task> findAllByUser(User user) {
        return findAll(user);
    }

    public List<Task> findAllAdmin() {
        return findAll(null);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task findByIdForUser(Long id, User user) {
        Task task = findById(id);

        if (user == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        if (!task.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta tarea");
        }

        return task;
    }

    public List<Task> findAllFiltered(User user, Boolean completed, LocalDate from, LocalDate to) {

        Sort sort = Sort.by("createdAt").ascending();

        if (user == null) {
            return findAllAdmin();
        }

        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt = (to != null) ? to.atTime(LocalTime.MAX) : null;

        List<Task> result;

        if (completed == null && fromDt == null && toDt == null) {
            result = taskRepository.findByAuthor(user, sort);

        } else if (completed != null && fromDt == null && toDt == null) {
            result = taskRepository.findByAuthorAndCompleted(user, completed, sort);

        } else if (completed == null && fromDt != null && toDt != null) {
            result = taskRepository.findByAuthorAndCreatedAtBetween(user, fromDt, toDt, sort);

        } else if (completed != null && fromDt != null && toDt != null) {
            result = taskRepository.findByAuthorAndCompletedAndCreatedAtBetween(user, completed, fromDt, toDt, sort);

        } else {
            if (fromDt == null) fromDt = LocalDate.of(1970, 1, 1).atStartOfDay();
            if (toDt == null) toDt = LocalDate.of(3000, 1, 1).atTime(LocalTime.MAX);

            if (completed == null) {
                result = taskRepository.findByAuthorAndCreatedAtBetween(user, fromDt, toDt, sort);
            } else {
                result = taskRepository.findByAuthorAndCompletedAndCreatedAtBetween(user, completed, fromDt, toDt, sort);
            }
        }

        if (result.isEmpty()) {
            throw new EmptyTaskListException();
        }

        return result;
    }

    public Task createTask(CreateTaskRequest req, User author) {
        return createOrEditTask(req, author);
    }

    public Task editTaskForUser(EditTaskRequest req, User user) {
        Task oldTask = findByIdForUser(req.getId(), user);
        return createOrEditTaskWithExisting(req, oldTask);
    }

    private Task createOrEditTask(CreateTaskRequest req, User author) {

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .DueDate(req.getDueDate())
                .build();

        if (req.getCategoryId() == null || req.getCategoryId() == -1L) {
            req.setCategoryId(1L);
        }

        Category category = categoryRepository.getReferenceById(req.getCategoryId());
        task.setCategory(category);

        String rawTags = req.getTags() != null ? req.getTags() : "";

        List<String> textTags = Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();

        if (!textTags.isEmpty()) {
            task.getTags().addAll(tagService.saveOrGet(textTags));
        }

        task.setAuthor(author);
        task.setCompleted(false);

        return taskRepository.save(task);
    }

    private Task createOrEditTaskWithExisting(EditTaskRequest req, Task oldTask) {

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .DueDate(req.getDueDate())
                .build();

        if (req.getCategoryId() == null || req.getCategoryId() == -1L) {
            req.setCategoryId(1L);
        }

        Category category = categoryRepository.getReferenceById(req.getCategoryId());
        task.setCategory(category);

        String rawTags = req.getTags() != null ? req.getTags() : "";

        List<String> textTags = Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();

        if (!textTags.isEmpty()) {
            task.getTags().addAll(tagService.saveOrGet(textTags));
        }

        task.setId(oldTask.getId());
        task.setCreatedAt(oldTask.getCreatedAt());
        task.setAuthor(oldTask.getAuthor());
        task.setCompleted(req.isCompleted());

        return taskRepository.save(task);
    }

    public Task toggleCompleteForUser(Long id, User user) {
        Task task = findByIdForUser(id, user);
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    public void deleteByIdForUser(Long id, User user) {
        Task task = findByIdForUser(id, user);
        taskRepository.delete(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> updateCategory(Category oldCategory, Category newCategory) {
        List<Task> tasks = taskRepository.findByCategory(oldCategory);
        tasks.forEach(t -> t.setCategory(newCategory));
        taskRepository.saveAll(tasks);
        return tasks;
    }
}