package com.openwebinars.todo.task.model;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User user, Sort sort);
    Page<Task> findByAuthor(User user, Pageable pageable);

    List<Task> findByCategory(Category category);

    List<Task> findByAuthorAndCompleted(User user, boolean completed, Sort sort);
    Page<Task> findByAuthorAndCompleted(User user, boolean completed, Pageable pageable);

    List<Task> findByAuthorAndCreatedAtBetween(User user, LocalDateTime from, LocalDateTime to, Sort sort);
    List<Task> findByAuthorAndCompletedAndCreatedAtBetween(User user, boolean completed, LocalDateTime from, LocalDateTime to, Sort sort);

    List<Task> findByAuthorAndTitleContainingIgnoreCase(User user, String title, Sort sort);
    Page<Task> findByAuthorAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);

    List<Task> findByAuthorAndCategory_Id(User user, Long categoryId, Sort sort);
    Page<Task> findByAuthorAndCategory_Id(User user, Long categoryId, Pageable pageable);

    List<Task> findByAuthorAndCompletedAndCategory_Id(User user, boolean completed, Long categoryId, Sort sort);
    Page<Task> findByAuthorAndCompletedAndCategory_Id(User user, boolean completed, Long categoryId, Pageable pageable);

    List<Task> findByAuthorAndTitleContainingIgnoreCaseAndCompleted(User user, String title, boolean completed, Sort sort);
    Page<Task> findByAuthorAndTitleContainingIgnoreCaseAndCompleted(User user, String title, boolean completed, Pageable pageable);

    List<Task> findByAuthorAndTitleContainingIgnoreCaseAndCategory_Id(User user, String title, Long categoryId, Sort sort);
    Page<Task> findByAuthorAndTitleContainingIgnoreCaseAndCategory_Id(User user, String title, Long categoryId, Pageable pageable);

    List<Task> findByAuthorAndTitleContainingIgnoreCaseAndCompletedAndCategory_Id(User user, String title, boolean completed, Long categoryId, Sort sort);
    Page<Task> findByAuthorAndTitleContainingIgnoreCaseAndCompletedAndCategory_Id(User user, String title, boolean completed, Long categoryId, Pageable pageable);

    long countByCompleted(boolean completed);

    @Query("""
           select t.author.fullname, count(t)
           from Task t
           group by t.author.fullname
           order by count(t) desc
           """)
    List<Object[]> findTopUsersByTaskCount();
}