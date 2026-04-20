package com.openwebinars.todo.task.dto;

public record TopUserTaskCount(
        String fullname,
        Long totalTasks
) {
}