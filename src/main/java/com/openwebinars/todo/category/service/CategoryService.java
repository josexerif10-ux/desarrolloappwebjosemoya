
package com.openwebinars.todo.category.service;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.category.model.CategoryRepository;
import com.openwebinars.todo.task.service.TaskService;
import com.openwebinars.todo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskService taskService;

    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("title").ascending());
    }

    public void deleteById(Long id) {
        if (id != 1L) {
            Category oldCategory = categoryRepository.getReferenceById(id);
            Category mainCategory = categoryRepository.getReferenceById(1L);
            taskService.updateCategory(oldCategory, mainCategory);
            categoryRepository.deleteById(id);
        }
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    public List<Category> findAllByUser(User user) {
        if (user == null) {
            return categoryRepository.findAll(Sort.by("title").ascending());
        }
        return categoryRepository.findByOwnerIsNullOrOwner(user, Sort.by("title").ascending());
    }

    public Category saveForUser(Category category, User user) {
        category.setOwner(user);
        return categoryRepository.save(category);
    }
}


