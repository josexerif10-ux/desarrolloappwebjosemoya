
package com.openwebinars.todo.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateTaskRequest {


    @NotBlank(message = "{task.title.notblank}")
    protected String title;
    protected String description;
    protected String tags;

    @NotNull(message = "{task.categoryId.notnull}")
    @Positive(message = "{task.categoryId.notnull}")
    protected Long categoryId;

}
