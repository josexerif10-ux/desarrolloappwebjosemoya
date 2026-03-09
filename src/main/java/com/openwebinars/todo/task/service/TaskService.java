
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

        /*public List<Task> findAll() {

        List<Task> result = taskRepository.findAll();

        if (result.isEmpty())
            throw new EmptyTaskListException();

        return result;

    }*/

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

    public List<Task> findAllFiltered(User user, Boolean completed, LocalDate from, LocalDate to) {

        Sort sort = Sort.by("createdAt").ascending();

        // si no te llega usuario, reutiliza tu admin
        if (user == null) {
            return findAllAdmin(); // mínimo cambio; si quieres filtros admin también lo hacemos
        }

        // Normaliza fechas si llegan (de LocalDate a LocalDateTime)
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
            // Si llega solo from o solo to, hacemos un rango razonable:
            if (fromDt == null) fromDt = LocalDate.of(1970, 1, 1).atStartOfDay();
            if (toDt == null) toDt = LocalDate.of(3000, 1, 1).atTime(LocalTime.MAX);

            if (completed == null)
                result = taskRepository.findByAuthorAndCreatedAtBetween(user, fromDt, toDt, sort);
            else
                result = taskRepository.findByAuthorAndCompletedAndCreatedAtBetween(user, completed, fromDt, toDt, sort);
        }

        if (result.isEmpty())
            throw new EmptyTaskListException();

        return result;
    }

    public Task createTask(CreateTaskRequest req, User author) {
        return createOrEditTask(req, author);
    }

    public Task editTask(EditTaskRequest req) {
        return createOrEditTask(req, null);
    }

    private Task createOrEditTask(CreateTaskRequest req, User author) {

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .build();

        if (req.getCategoryId() == null || req.getCategoryId() == -1L)
            req.setCategoryId(1L);
        Category category = categoryRepository.getReferenceById(req.getCategoryId());
        if (category == null) // Categoría por defecto
            category = categoryRepository.getReferenceById(1L);

        task.setCategory(category);

        // Procesamos los tags que vienen en forma de tag1,tag2,tag3
        List<String> textTags = Arrays.stream(req.getTags().split(","))
                .map(String::trim)
                .toList();
        // Los añadimos a task
        task.getTags().addAll(tagService.saveOrGet(textTags));

        // Esto solamente se procesa si queremos editar un Task
        if (req instanceof EditTaskRequest editReq) {
            Task oldTask = findById(editReq.getId());
            task.setId(oldTask.getId());
            task.setCreatedAt(oldTask.getCreatedAt());
            task.setAuthor(oldTask.getAuthor());
            task.setCompleted(editReq.isCompleted());
        } else {
            task.setAuthor(author);
        }

        // Inserta o actualiza, según corresponda
        return taskRepository.save(task);

    }

    public Task toggleComplete(Long id) {
        Task task = findById(id);
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
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
