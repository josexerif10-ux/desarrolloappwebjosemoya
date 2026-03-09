
package com.openwebinars.todo.shared.init;

import com.openwebinars.todo.category.model.Category;
import com.openwebinars.todo.category.model.CategoryRepository;
import com.openwebinars.todo.task.dto.CreateTaskRequest;
import com.openwebinars.todo.task.service.TaskService;
import com.openwebinars.todo.user.dto.CreateUserRequest;
import com.openwebinars.todo.user.model.User;
import com.openwebinars.todo.user.model.UserRole;
import com.openwebinars.todo.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeed {

    private final CategoryRepository categoryRepository;
    private final TaskService taskService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        insertCategories();
        List<User> users =insertUsers();
        insertTasks(users.get(0));
    }

    /*
        Solamente devuelve aquellos que son UserRole.USER
        para poder usarlos como autores de Task
     */
    private List<User> insertUsers() {

        List<User> result = new ArrayList<>();

        CreateUserRequest req = CreateUserRequest.builder()
                .username("user")
                .email("user@user.com")
                .password("1234")
                .verifyPassword("1234")
                .fullname("The user")
                .build();
        User user = userService.registerUser(req);
        result.add(user);

        CreateUserRequest req2 = CreateUserRequest.builder()
                .username("admin")
                .email("admin@openwebinars.net")
                .password("admin123")
                .verifyPassword("admin123")
                .fullname("Administrador")
                .build();
        User user2 = userService.registerUser(req2);

        userService.changeRole(user2, UserRole.ADMIN);

        return result;
    }

    private void insertCategories() {
        categoryRepository.save(Category.builder().title("Main").build());
    }

    private void insertTasks(User author) {

        CreateTaskRequest req1 = CreateTaskRequest.builder()
                .title("First task!")
                .description("Lorem ipsum dolor sit amet")
                .tags("tag1,tag2,tag3")
                .build();

        taskService.createTask(req1, author);

        CreateTaskRequest req2 = CreateTaskRequest.builder()
                .title("Second task!")
                .description("Lorem ipsum dolor sit amet")
                .tags("tag1,tag2,tag4")
                .build();

        taskService.createTask(req2, author);

    }
}
