
package com.openwebinars.todo.task.model;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.user.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAuthor(User user, Sort sort);
    List<Task> findByCategory(Category category);

    //  Filtro por completadas
    List<Task> findByAuthorAndCompleted(User user, boolean completed, Sort sort);

    // Filtro por fecha (asumiendo createdAt es LocalDateTime)
    List<Task> findByAuthorAndCreatedAtBetween(User user, LocalDateTime from, LocalDateTime to, Sort sort);

    //  Combinado (completado + fechas)
    List<Task> findByAuthorAndCompletedAndCreatedAtBetween(User user, boolean completed, LocalDateTime from, LocalDateTime to, Sort sort);
}
