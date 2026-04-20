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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        if (task.getAuthor() == null || task.getAuthor().getId() == null) {
            throw new AccessDeniedException("La tarea no tiene autor asignado");
        }

        if (!task.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta tarea");
        }

        return task;
    }

    public List<Task> findAllFiltered(User user, String title, Boolean completed, Long categoryId, LocalDate from, LocalDate to) {

        Sort sort = Sort.by("createdAt").ascending();

        if (user == null) {
            return findAllAdmin();
        }

        String safeTitle = (title != null) ? title.trim() : "";
        boolean hasTitle = !safeTitle.isBlank();
        boolean hasCategory = categoryId != null && categoryId > 0;

        List<Task> result;

        if (!hasTitle && completed == null && !hasCategory) {
            result = taskRepository.findByAuthor(user, sort);

        } else if (hasTitle && completed == null && !hasCategory) {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCase(user, safeTitle, sort);

        } else if (!hasTitle && completed != null && !hasCategory) {
            result = taskRepository.findByAuthorAndCompleted(user, completed, sort);

        } else if (!hasTitle && completed == null && hasCategory) {
            result = taskRepository.findByAuthorAndCategory_Id(user, categoryId, sort);

        } else if (!hasTitle && completed != null && hasCategory) {
            result = taskRepository.findByAuthorAndCompletedAndCategory_Id(user, completed, categoryId, sort);

        } else if (hasTitle && completed != null && !hasCategory) {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCaseAndCompleted(user, safeTitle, completed, sort);

        } else if (hasTitle && completed == null && hasCategory) {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCaseAndCategory_Id(user, safeTitle, categoryId, sort);

        } else {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCaseAndCompletedAndCategory_Id(user, safeTitle, completed, categoryId, sort);
        }

        if (from != null) {
            LocalDateTime fromDt = from.atStartOfDay();
            result = result.stream()
                    .filter(task -> !task.getCreatedAt().isBefore(fromDt))
                    .toList();
        }

        if (to != null) {
            LocalDateTime toDt = to.atTime(LocalTime.MAX);
            result = result.stream()
                    .filter(task -> !task.getCreatedAt().isAfter(toDt))
                    .toList();
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
    public Page<Task> findPageFiltered(User user, String title, Boolean completed, Long categoryId,
                                       LocalDate from, LocalDate to, int page, int size) {

        if (user == null) {
            throw new EmptyTaskListException();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        String safeTitle = (title != null) ? title.trim() : "";
        boolean hasTitle = !safeTitle.isBlank();
        boolean hasCategory = categoryId != null && categoryId > 0;

        Page<Task> result;

        if (!hasTitle && completed == null && !hasCategory) {
            result = taskRepository.findByAuthor(user, pageable);

        } else if (hasTitle && completed == null && !hasCategory) {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCase(user, safeTitle, pageable);

        } else if (!hasTitle && completed != null && !hasCategory) {
            result = taskRepository.findByAuthorAndCompleted(user, completed, pageable);

        } else if (!hasTitle && completed == null && hasCategory) {
            result = taskRepository.findByAuthorAndCategory_Id(user, categoryId, pageable);

        } else if (!hasTitle && completed != null && hasCategory) {
            result = taskRepository.findByAuthorAndCompletedAndCategory_Id(user, completed, categoryId, pageable);

        } else if (hasTitle && completed != null && !hasCategory) {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCaseAndCompleted(user, safeTitle, completed, pageable);

        } else if (hasTitle && completed == null && hasCategory) {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCaseAndCategory_Id(user, safeTitle, categoryId, pageable);

        } else {
            result = taskRepository.findByAuthorAndTitleContainingIgnoreCaseAndCompletedAndCategory_Id(user, safeTitle, completed, categoryId, pageable);
        }

        // Filtrado extra por fecha sobre el contenido actual
        if (from != null || to != null) {
            List<Task> filtered = result.getContent().stream()
                    .filter(task -> {
                        boolean ok = true;
                        if (from != null) {
                            ok = ok && !task.getCreatedAt().isBefore(from.atStartOfDay());
                        }
                        if (to != null) {
                            ok = ok && !task.getCreatedAt().isAfter(to.atTime(LocalTime.MAX));
                        }
                        return ok;
                    })
                    .toList();

            if (filtered.isEmpty()) {
                throw new EmptyTaskListException();
            }

            return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
        }

        if (result.isEmpty()) {
            throw new EmptyTaskListException();
        }

        return result;
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