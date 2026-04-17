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

    List<Task> findByAuthorAndCompleted(User user, boolean completed, Sort sort);

    List<Task> findByAuthorAndCreatedAtBetween(User user, LocalDateTime from, LocalDateTime to, Sort sort);

    List<Task> findByAuthorAndCompletedAndCreatedAtBetween(User user, boolean completed, LocalDateTime from, LocalDateTime to, Sort sort);

    List<Task> findByAuthorAndTitleContainingIgnoreCase(User user, String title, Sort sort);

    List<Task> findByAuthorAndCategory_Id(User user, Long categoryId, Sort sort);

    List<Task> findByAuthorAndCompletedAndCategory_Id(User user, boolean completed, Long categoryId, Sort sort);

    List<Task> findByAuthorAndTitleContainingIgnoreCaseAndCompleted(User user, String title, boolean completed, Sort sort);

    List<Task> findByAuthorAndTitleContainingIgnoreCaseAndCategory_Id(User user, String title, Long categoryId, Sort sort);

    List<Task> findByAuthorAndTitleContainingIgnoreCaseAndCompletedAndCategory_Id(User user, String title, boolean completed, Long categoryId, Sort sort);
}