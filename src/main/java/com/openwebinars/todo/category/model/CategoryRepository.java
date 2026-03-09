
package com.openwebinars.todo.category.model;

import com.openwebinars.todo.user.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    List<Category> findByOwner(User owner, Sort sort);

    // Para incluir también la Main (owner null) + las del usuario
    List<Category> findByOwnerIsNullOrOwner(User owner, Sort sort);
}

